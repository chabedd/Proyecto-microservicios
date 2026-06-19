package com.microservice.abastecimiento_service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.microservice.abastecimiento_service.dto.DetalleOrdenCompraRequestDTO;
import com.microservice.abastecimiento_service.dto.OrdenCompraRequestDTO;
import com.microservice.abastecimiento_service.dto.OrdenCompraResponseDTO;
import com.microservice.abastecimiento_service.dto.ProductoResponseDTO;
import com.microservice.abastecimiento_service.exception.ManejadorGlobal.RecursoNoEncontradoException;
import com.microservice.abastecimiento_service.exception.ManejadorGlobal.ReglaDeNegocioException;
import com.microservice.abastecimiento_service.exception.ManejadorGlobal.ValidacionException;
import com.microservice.abastecimiento_service.feignclient.AjusteStockDTO;
import com.microservice.abastecimiento_service.feignclient.InventarioClient;
import com.microservice.abastecimiento_service.feignclient.ProductoClient;
import com.microservice.abastecimiento_service.feignclient.ProveedorClient;
import com.microservice.abastecimiento_service.feignclient.ProveedorResponseDTO;
import com.microservice.abastecimiento_service.model.DetalleOrdenCompra;
import com.microservice.abastecimiento_service.model.OrdenCompra;
import com.microservice.abastecimiento_service.model.TipoEstado;
import com.microservice.abastecimiento_service.repository.OrdenCompraRepository;
import com.microservice.abastecimiento_service.service.AbastecimientoService;

@ExtendWith(MockitoExtension.class)
class AbastecimientoServiceTest {

    @Mock
    private OrdenCompraRepository repository;
    @Mock
    private InventarioClient inventarioClient;
    @Mock
    private ProductoClient productoClient;
    @Mock
    private ProveedorClient proveedorClient;

    @InjectMocks
    private AbastecimientoService service;

    // Variables globales para la prueba
    private OrdenCompraRequestDTO requestDTO;
    private OrdenCompra ordenPendiente;
    private OrdenCompra ordenAprobada;
    private ProveedorResponseDTO proveedorActivo;
    private ProductoResponseDTO productoActivo;

    @BeforeEach
    void setUp() {
        // 1. Preparar DTO de Request
        DetalleOrdenCompraRequestDTO detalleReq = new DetalleOrdenCompraRequestDTO();
        detalleReq.setProductoId(100L);
        detalleReq.setCantidad(50);
        detalleReq.setPrecioUnitario(1000.0);

        requestDTO = new OrdenCompraRequestDTO();
        requestDTO.setProveedorId(10L);
        requestDTO.setDetalles(List.of(detalleReq));

        // 2. Preparar DTOs de Microservicios externos
        proveedorActivo = new ProveedorResponseDTO();
        proveedorActivo.setId(10L);
        proveedorActivo.setActivo(true);

        productoActivo = new ProductoResponseDTO();
        productoActivo.setId(100L);
        productoActivo.setActivo(true);

        // 3. Preparar Entidades de Base de datos
        DetalleOrdenCompra detalle = new DetalleOrdenCompra();
        detalle.setId(1L);
        detalle.setProductoId(100L);
        detalle.setCantidad(50);
        detalle.setPrecioUnitario(1000.0);

        ordenPendiente = new OrdenCompra();
        ordenPendiente.setId(1L);
        ordenPendiente.setProveedorId(10L);
        ordenPendiente.setEstado(TipoEstado.PENDIENTE);
        ordenPendiente.setDetalles(new ArrayList<>(List.of(detalle))); // Inicializamos lista mutable

        ordenAprobada = new OrdenCompra();
        ordenAprobada.setId(2L);
        ordenAprobada.setProveedorId(10L);
        ordenAprobada.setEstado(TipoEstado.APROBADA);
        ordenAprobada.setDetalles(new ArrayList<>(List.of(detalle)));
    }

    // ==========================================
    // TESTS PARA CREAR ORDEN
    // ==========================================

    @Test
    void crearOrden_conProveedorYProductoActivos_deberiaCrearOrden() {
        // Preparar
        when(proveedorClient.obtenerProveedorPorId(10L)).thenReturn(proveedorActivo);
        when(productoClient.obtenerProductoPorId(100L)).thenReturn(productoActivo);
        when(repository.save(any(OrdenCompra.class))).thenReturn(ordenPendiente);

        // Actuar
        OrdenCompraResponseDTO response = service.crearOrden(requestDTO);

        // Afirmar
        assertNotNull(response);
        assertEquals(TipoEstado.PENDIENTE, response.getEstado());
        verify(repository, times(1)).save(any(OrdenCompra.class));
    }

    @Test
    void crearOrden_conProveedorInactivo_deberiaLanzarExcepcion() {
        // Preparar
        proveedorActivo.setActivo(false); // Inactivamos el proveedor
        when(proveedorClient.obtenerProveedorPorId(10L)).thenReturn(proveedorActivo);

        // Actuar y Afirmar
        assertThrows(ReglaDeNegocioException.class, () -> service.crearOrden(requestDTO));
        verify(repository, never()).save(any()); // No debe guardar nada
    }

    @Test
    void crearOrden_conProductoInactivo_deberiaLanzarExcepcion() {
        // Preparar
        productoActivo.setActivo(false); // Inactivamos el producto
        when(proveedorClient.obtenerProveedorPorId(10L)).thenReturn(proveedorActivo);
        when(productoClient.obtenerProductoPorId(100L)).thenReturn(productoActivo);

        // Actuar y Afirmar
        assertThrows(ReglaDeNegocioException.class, () -> service.crearOrden(requestDTO));
        verify(repository, never()).save(any());
    }

  
    // TESTS PARA CAMBIAR ESTADO Y MÁQUINA DE ESTADOS
   

    @Test
    void cambiarEstado_dePendienteAAprobada_deberiaCambiarEstado() {
        // Preparar
        when(repository.findById(1L)).thenReturn(Optional.of(ordenPendiente));
        when(repository.save(any(OrdenCompra.class))).thenReturn(ordenPendiente);

        // Actuar
        OrdenCompraResponseDTO response = service.cambiarEstado(1L, "APROBADA", null);

        // Afirmar
        assertEquals(TipoEstado.APROBADA, response.getEstado());
        verify(inventarioClient, never()).ajustarStock(any()); // No debe llamar a inventario
    }

    @Test
    void cambiarEstado_deAprobadaARecibida_deberiaAjustarStock() {
        // Preparar
        Long bodegaId = 5L;
        when(repository.findById(2L)).thenReturn(Optional.of(ordenAprobada)); // Partimos de APROBADA
        when(repository.save(any(OrdenCompra.class))).thenReturn(ordenAprobada);

        // Actuar
        OrdenCompraResponseDTO response = service.cambiarEstado(2L, "RECIBIDA", bodegaId);

        // Afirmar
        assertEquals(TipoEstado.RECIBIDA, response.getEstado());
        // Verifica que se llamó al microservicio de inventario EXACTAMENTE UNA VEZ (por el 1 producto en el detalle)
        verify(inventarioClient, times(1)).ajustarStock(any(AjusteStockDTO.class)); 
    }

    @Test
    void cambiarEstado_aRecibidaSinBodegaId_deberiaLanzarExcepcion() {
        // Preparar
        when(repository.findById(2L)).thenReturn(Optional.of(ordenAprobada));

        // Actuar y Afirmar: Intentamos pasar a RECIBIDA enviando null en bodegaId
        assertThrows(ValidacionException.class, () -> service.cambiarEstado(2L, "RECIBIDA", null));
        verify(inventarioClient, never()).ajustarStock(any());
        verify(repository, never()).save(any());
    }

    @Test
    void cambiarEstado_transicionInvalida_deberiaLanzarExcepcion() {
        // Preparar
        when(repository.findById(1L)).thenReturn(Optional.of(ordenPendiente));

        // Actuar y Afirmar: Una orden PENDIENTE no puede pasar directo a RECIBIDA
        assertThrows(ReglaDeNegocioException.class, () -> service.cambiarEstado(1L, "RECIBIDA", 5L));
    }

    @Test
    void cambiarEstado_estadoInvalido_deberiaLanzarExcepcion() {
        // Preparar
        when(repository.findById(1L)).thenReturn(Optional.of(ordenPendiente));

        // Actuar y Afirmar: Pasamos un string que no existe en el enum
        assertThrows(ValidacionException.class, () -> service.cambiarEstado(1L, "ESTADO_INVENTADO", null));
    }

    // TESTS PARA OBTENER Y MISCELÁNEOS
  
    @Test
    void obtenerPorId_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    void cancelarOrden_conTransicionValida_deberiaCancelar() {
        when(repository.findById(1L)).thenReturn(Optional.of(ordenPendiente));
        
        service.cancelarOrden(1L);

        assertEquals(TipoEstado.CANCELADA, ordenPendiente.getEstado());
        verify(repository).save(ordenPendiente);
    }

    @Test
    void tieneOrdenesActivasPorProveedor_deberiaRetornarLoQueDigaElRepo() {
        when(repository.existsByProveedorIdAndEstadoIn(eq(10L), anyList())).thenReturn(true);
        
        boolean resultado = service.tieneOrdenesActivasPorProveedor(10L);
        
        assertTrue(resultado);
    }
}