package com.microservice.producto_service;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.microservice.producto_service.dto.ProductoRequestDTO;
import com.microservice.producto_service.dto.ProductoResponseDTO;
import com.microservice.producto_service.exception.ManejadorGlobal.ProductoConStockException;
import com.microservice.producto_service.exception.ManejadorGlobal.ProductoNoEncontradoException;
import com.microservice.producto_service.exception.ManejadorGlobal.ProductoValidacionException;
import com.microservice.producto_service.exception.ManejadorGlobal.SkuDuplicadoException;
import com.microservice.producto_service.feignClient.InventarioClient;
import com.microservice.producto_service.feignClient.InventarioResponseDTO;
import com.microservice.producto_service.model.Producto;
import com.microservice.producto_service.repository.ProductoRepository;
import com.microservice.producto_service.service.ProductoService;
import com.microservice.producto_service.validation.ProductoValidator;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @Mock
    private InventarioClient inventarioClient;

    @Mock
    private ProductoValidator productoValidator;

    @InjectMocks
    private ProductoService service;

    private ProductoRequestDTO requestDTO;
    private Producto productoEntity;

    @BeforeEach
    void setUp() {
        // Datos de prueba reutilizables
        requestDTO = new ProductoRequestDTO("SKU123", "Zapatillas", 50000.0, 1L);
        
        productoEntity = new Producto();
        productoEntity.setId(1L);
        productoEntity.setCodigo("SKU123");
        productoEntity.setNombre("Zapatillas");
        productoEntity.setPrecioBase(50000.0);
        productoEntity.setProveedorId(1L);
        productoEntity.setActivo(true);
    }

    // ==========================================
    // TESTS PARA CREAR
    // ==========================================

    @Test
    void crear_conDatosValidos_deberiaRetornarProducto() {
        // Preparar
        when(repository.existsByCodigo(requestDTO.getCodigo())).thenReturn(false);
        when(repository.save(any(Producto.class))).thenReturn(productoEntity);

        // Actuar
        ProductoResponseDTO response = service.crear(requestDTO);

        // Afirmar
        assertNotNull(response);
        assertEquals("SKU123", response.getCodigo());
        assertEquals("Zapatillas", response.getNombre());
        verify(repository, times(1)).save(any(Producto.class)); // Verifica que se llamó al save
    }

    @Test
    void crear_conPrecioInvalido_deberiaLanzarExcepcion() {
        // Preparar
        requestDTO.setPrecioBase(0.0); // Precio inválido
        doThrow(new ProductoValidacionException("El precio base debe ser mayor a 0."))
            .when(productoValidator).validarPrecioBase(0.0);

        // Actuar y Afirmar
        ProductoValidacionException exception = assertThrows(
            ProductoValidacionException.class, 
            () -> service.crear(requestDTO)
        );
        assertEquals("El precio base debe ser mayor a 0.", exception.getMessage());
        verify(repository, never()).save(any(Producto.class)); // Asegura que no se guardó nada en BD
    }

    @Test
    void crear_conSkuDuplicado_deberiaLanzarExcepcion() {
        // Preparar
        when(repository.existsByCodigo(requestDTO.getCodigo())).thenReturn(true);
        doThrow(new SkuDuplicadoException("Ya existe un producto con el SKU: SKU123"))
            .when(productoValidator).validarCodigoDuplicado(true, "SKU123");

        // Actuar y Afirmar
        assertThrows(SkuDuplicadoException.class, () -> service.crear(requestDTO));
        verify(repository, never()).save(any());
    }


    // TESTS PARA OBTENER
  

    @Test
    void obtenerPorId_cuandoExiste_deberiaRetornarProducto() {
        // Preparar
        when(repository.findById(1L)).thenReturn(Optional.of(productoEntity));

        // Actuar
        ProductoResponseDTO response = service.obtenerPorId(1L);

        // Afirmar
        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void obtenerPorId_cuandoNoExiste_deberiaLanzarExcepcion() {
        // Preparar
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Actuar y Afirmar
        assertThrows(ProductoNoEncontradoException.class, () -> service.obtenerPorId(99L));
    }

    
    // TESTS PARA DESACTIVAR (Con Feign Client)

    @Test
    void desactivar_sinStockActivo_deberiaDesactivarProducto() {
        // Preparar
        when(repository.findById(1L)).thenReturn(Optional.of(productoEntity));
        
        var inv1 = new InventarioResponseDTO(); 
        inv1.setStockActual(0); // Sin stock
        
        when(inventarioClient.obtenerInventariosPorProducto(1L)).thenReturn(List.of(inv1));

        // Actuar
        service.desactivar(1L);

        // Afirmar
        assertFalse(productoEntity.getActivo()); // Verifica que el estado cambió a false
        verify(repository).save(productoEntity); // Verifica que se guardó el cambio
    }

    @Test
    void desactivar_conStockActivo_deberiaLanzarExcepcion() {
        // Preparar
        when(repository.findById(1L)).thenReturn(Optional.of(productoEntity));
        
        var invConStock = new InventarioResponseDTO();
        invConStock.setStockActual(10); // Tiene stock!
        
        when(inventarioClient.obtenerInventariosPorProducto(1L)).thenReturn(List.of(invConStock));

        // Actuar y Afirmar
        assertThrows(ProductoConStockException.class, () -> service.desactivar(1L));
        assertTrue(productoEntity.getActivo()); // El estado no debió cambiar
        verify(repository, never()).save(any()); // No se debió guardar nada
    }
}