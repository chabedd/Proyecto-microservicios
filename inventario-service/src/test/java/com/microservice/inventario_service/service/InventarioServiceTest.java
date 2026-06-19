package com.microservice.inventario_service.service;

import java.time.LocalDateTime;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.microservice.inventario_service.dto.InventarioRequestDTO;
import com.microservice.inventario_service.dto.InventarioResponseDTO;
import com.microservice.inventario_service.exception.ManejadorGlobal.InventarioNoEncontradoException;
import com.microservice.inventario_service.feignclient.BodegaClient;
import com.microservice.inventario_service.feignclient.FeignSupport.BodegaDTO;
import com.microservice.inventario_service.feignclient.FeignSupport.ProductoDTO;
import com.microservice.inventario_service.feignclient.ProductoClient;
import com.microservice.inventario_service.mapper.InventarioMapper;
import com.microservice.inventario_service.model.Inventario;
import com.microservice.inventario_service.repository.InventarioRepository;
import com.microservice.inventario_service.validation.InventarioValidator;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock private InventarioRepository inventarioRepository;
    @Mock private InventarioMapper inventarioMapper;
    @Mock private ProductoClient productoClient;
    @Mock private BodegaClient bodegaClient;
    @Mock private InventarioValidator inventarioValidator;

    @InjectMocks
    private InventarioService inventarioService;

    private Inventario inventario;
    private InventarioResponseDTO responseDTO;
    private BodegaDTO bodegaDTO;

    @BeforeEach
    void setUp() {
        inventario = new Inventario();
        inventario.setId(1L);
        inventario.setProductoId(10L);
        inventario.setBodegaId(20L);
        inventario.setStockActual(100);
        inventario.setUltimaActualizacion(LocalDateTime.now());

        responseDTO = new InventarioResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setProductoId(10L);
        responseDTO.setBodegaId(20L);
        responseDTO.setStockActual(100);

        bodegaDTO = new BodegaDTO(20L, "Bodega Central", "Santiago", 500L);
    }

    // ── crearInventario ───────────────────────────────────────────────────────

    @Test
    void crearInventario_DatosValidos_RetornaResponseDTO() {
        InventarioRequestDTO request = new InventarioRequestDTO();
        request.setProductoId(10L);
        request.setBodegaId(20L);
        request.setStockActual(100);

        when(inventarioMapper.toEntity(request)).thenReturn(inventario);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(new ProductoDTO());
        when(bodegaClient.obtenerBodegaPorId(20L)).thenReturn(bodegaDTO);
        doNothing().when(inventarioValidator).validarBodegaActiva(bodegaDTO, 20L);
        when(inventarioRepository.findByProductoIdAndBodegaId(10L, 20L)).thenReturn(Optional.empty());
        doNothing().when(inventarioValidator).validarRegistroDuplicado(false);
        doNothing().when(inventarioValidator).validarStockNoNegativo(100);
        when(inventarioRepository.save(inventario)).thenReturn(inventario);
        when(inventarioMapper.toResponseDTO(inventario)).thenReturn(responseDTO);

        InventarioResponseDTO result = inventarioService.crearInventario(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100, result.getStockActual());
    }

    @Test
    void crearInventario_RegistroDuplicado_LanzaExcepcion() {
        InventarioRequestDTO request = new InventarioRequestDTO();
        request.setProductoId(10L);
        request.setBodegaId(20L);
        request.setStockActual(50);

        when(inventarioMapper.toEntity(request)).thenReturn(inventario);
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(new ProductoDTO());
        when(bodegaClient.obtenerBodegaPorId(20L)).thenReturn(bodegaDTO);
        doNothing().when(inventarioValidator).validarBodegaActiva(bodegaDTO, 20L);
        when(inventarioRepository.findByProductoIdAndBodegaId(10L, 20L)).thenReturn(Optional.of(inventario));
        doThrow(new IllegalArgumentException("Ya existe un registro de inventario para este producto y bodega."))
                .when(inventarioValidator).validarRegistroDuplicado(true);

        assertThrows(IllegalArgumentException.class, () -> inventarioService.crearInventario(request));
        verify(inventarioRepository, never()).save(any());
    }

    // ── obtenerInventarioPorId ────────────────────────────────────────────────

    @Test
    void obtenerInventarioPorId_Existente_RetornaResponseDTO() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        when(inventarioMapper.toResponseDTO(inventario)).thenReturn(responseDTO);

        InventarioResponseDTO result = inventarioService.obtenerInventarioPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void obtenerInventarioPorId_NoExistente_LanzaInventarioNoEncontradoException() {
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(InventarioNoEncontradoException.class,
                () -> inventarioService.obtenerInventarioPorId(99L));
    }

    // ── obtenerTodosInventarios ───────────────────────────────────────────────

    @Test
    void obtenerTodosInventarios_HayRegistros_RetornaLista() {
        when(inventarioRepository.findAll()).thenReturn(List.of(inventario));
        when(inventarioMapper.toResponseDTOList(List.of(inventario))).thenReturn(List.of(responseDTO));

        List<InventarioResponseDTO> result = inventarioService.obtenerTodosInventarios();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void obtenerTodosInventarios_SinRegistros_RetornaListaVacia() {
        when(inventarioRepository.findAll()).thenReturn(List.of());
        when(inventarioMapper.toResponseDTOList(List.of())).thenReturn(List.of());

        List<InventarioResponseDTO> result = inventarioService.obtenerTodosInventarios();

        assertTrue(result.isEmpty());
    }

    // ── obtenerInventariosPorProducto ─────────────────────────────────────────

    @Test
    void obtenerInventariosPorProducto_ConResultados_RetornaLista() {
        when(inventarioRepository.findByProductoId(10L)).thenReturn(List.of(inventario));
        when(inventarioMapper.toResponseDTOList(List.of(inventario))).thenReturn(List.of(responseDTO));

        List<InventarioResponseDTO> result = inventarioService.obtenerInventariosPorProducto(10L);

        assertEquals(1, result.size());
    }

    // ── obtenerInventariosPorBodega ───────────────────────────────────────────

    @Test
    void obtenerInventariosPorBodega_ConResultados_RetornaLista() {
        when(inventarioRepository.findByBodegaId(20L)).thenReturn(List.of(inventario));
        when(inventarioMapper.toResponseDTOList(List.of(inventario))).thenReturn(List.of(responseDTO));

        List<InventarioResponseDTO> result = inventarioService.obtenerInventariosPorBodega(20L);

        assertEquals(1, result.size());
    }

    // ── actualizarStock ───────────────────────────────────────────────────────

    @Test
    void actualizarStock_Valido_RetornaActualizado() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        doNothing().when(inventarioValidator).validarStockNoNegativo(150);
        when(inventarioRepository.save(inventario)).thenReturn(inventario);
        when(inventarioMapper.toResponseDTO(inventario)).thenReturn(responseDTO);

        InventarioResponseDTO result = inventarioService.actualizarStock(1L, 150);

        assertNotNull(result);
        verify(inventarioRepository).save(inventario);
    }
    /*
    @Test
    void actualizarStock_StockNegativo_LanzaExcepcion() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));
        doThrow(new IllegalArgumentException("El stock actual no puede ser menor a cero."))
                .when(inventarioValidator).validarStockNoNegativo(-5);

        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.actualizarStock(1L, -5));
        verify(inventarioRepository, never()).save(any());
    }
    */

    @Test
    void actualizarStock_InventarioNoExistente_LanzaInventarioNoEncontradoException() {
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(InventarioNoEncontradoException.class,
                () -> inventarioService.actualizarStock(99L, 50));
    }

    // ── eliminarInventario ────────────────────────────────────────────────────

    @Test
    void eliminarInventario_Existente_EliminaCorrectamente() {
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inventario));

        inventarioService.eliminarInventario(1L);

        verify(inventarioRepository).deleteById(1L);
    }

    @Test
    void eliminarInventario_NoExistente_LanzaInventarioNoEncontradoException() {
        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(InventarioNoEncontradoException.class,
                () -> inventarioService.eliminarInventario(99L));
        verify(inventarioRepository, never()).deleteById(any());
    }

    // ── ajustarStock ──────────────────────────────────────────────────────────

    @Test
    void ajustarStock_InventarioExistente_SumaCorrectamente() {
        when(bodegaClient.obtenerBodegaPorId(20L)).thenReturn(bodegaDTO);
        doNothing().when(inventarioValidator).validarBodegaActiva(bodegaDTO, 20L);
        when(inventarioRepository.findByProductoIdAndBodegaId(10L, 20L)).thenReturn(Optional.of(inventario));
        doNothing().when(inventarioValidator).validarStockNoNegativo(110);
        when(inventarioRepository.save(inventario)).thenReturn(inventario);

        InventarioResponseDTO expected = new InventarioResponseDTO();
        expected.setStockActual(110);
        when(inventarioMapper.toResponseDTO(inventario)).thenReturn(expected);

        InventarioResponseDTO result = inventarioService.ajustarStock(10L, 20L, 10);

        assertNotNull(result);
        verify(inventarioRepository).save(inventario);
    }

    @Test
    void ajustarStock_InventarioNoExistente_CreaUnoNuevo() {
        when(bodegaClient.obtenerBodegaPorId(20L)).thenReturn(bodegaDTO);
        doNothing().when(inventarioValidator).validarBodegaActiva(bodegaDTO, 20L);
        when(inventarioRepository.findByProductoIdAndBodegaId(10L, 20L)).thenReturn(Optional.empty());
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(new ProductoDTO());
        doNothing().when(inventarioValidator).validarStockNoNegativo(50);

        Inventario nuevo = new Inventario();
        nuevo.setProductoId(10L);
        nuevo.setBodegaId(20L);
        nuevo.setStockActual(50);
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(nuevo);
        when(inventarioMapper.toResponseDTO(nuevo)).thenReturn(responseDTO);

        InventarioResponseDTO result = inventarioService.ajustarStock(10L, 20L, 50);

        assertNotNull(result);
        verify(inventarioRepository).save(any(Inventario.class));
    }

    @Test
    void ajustarStock_ResultadoNegativo_LanzaExcepcion() {
        when(bodegaClient.obtenerBodegaPorId(20L)).thenReturn(bodegaDTO);
        doNothing().when(inventarioValidator).validarBodegaActiva(bodegaDTO, 20L);
        when(inventarioRepository.findByProductoIdAndBodegaId(10L, 20L)).thenReturn(Optional.of(inventario));
        // stock actual = 100, delta = -200 => resultado = -100
        doThrow(new IllegalArgumentException("El stock actual no puede ser menor a cero."))
                .when(inventarioValidator).validarStockNoNegativo(-100);

        assertThrows(IllegalArgumentException.class,
                () -> inventarioService.ajustarStock(10L, 20L, -200));
    }
}
