package com.microservice.movimiento_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.microservice.movimiento_service.dto.MovimientoRequestDTO;
import com.microservice.movimiento_service.dto.MovimientoResponseDTO;
import com.microservice.movimiento_service.exception.ManejadorGlobal.MovimientoNoEncontradoException;
import com.microservice.movimiento_service.exception.ManejadorGlobal.ValidacionMovimientoException;
import com.microservice.movimiento_service.feignClient.AjusteStockDTO;
import com.microservice.movimiento_service.feignClient.InventarioClient;
import com.microservice.movimiento_service.model.Movimiento;
import com.microservice.movimiento_service.model.TipoMovimiento;
import com.microservice.movimiento_service.repository.MovimientoRepository;
import com.microservice.movimiento_service.validation.MovimientoValidator;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private MovimientoRepository repository;

    @Mock
    private InventarioClient inventarioClient;

    @Mock
    private MovimientoValidator movimientoValidator;

    @InjectMocks
    private MovimientoService service;

    private Movimiento movimientoDePrueba(Long id, TipoMovimiento tipo, Long origenId, Long destinoId) {
        Movimiento m = new Movimiento();
        m.setId(id);
        m.setProductoId(1L);
        m.setBodegaOrigenId(origenId);
        m.setBodegaDestinoId(destinoId);
        m.setTipo(tipo);
        m.setCantidad(50);
        m.setMotivo("Motivo de prueba");
        m.setFecha(LocalDateTime.now());
        return m;
    }

    private MovimientoRequestDTO requestDePrueba(TipoMovimiento tipo, Long origenId, Long destinoId) {
        MovimientoRequestDTO dto = new MovimientoRequestDTO();
        dto.setProductoId(1L);
        dto.setBodegaOrigenId(origenId);
        dto.setBodegaDestinoId(destinoId);
        dto.setTipo(tipo);
        dto.setCantidad(50);
        dto.setMotivo("Motivo de prueba");
        return dto;
    }

    // ---- registrar() - ENTRADA ----

    @Test
    void registrar_entrada_deberiaGuardarYAjustarStockEnDestino() {
        MovimientoRequestDTO request = requestDePrueba(TipoMovimiento.ENTRADA, null, 2L);
        Movimiento guardado = movimientoDePrueba(1L, TipoMovimiento.ENTRADA, null, 2L);
        doNothing().when(movimientoValidator).validarCamposBodega(any(), any(), any());
        when(repository.save(any(Movimiento.class))).thenReturn(guardado);

        MovimientoResponseDTO resultado = service.registrar(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(TipoMovimiento.ENTRADA, resultado.getTipo());
        assertEquals(50, resultado.getCantidad());
        verify(movimientoValidator).validarCamposBodega(TipoMovimiento.ENTRADA, null, 2L);
        verify(repository).save(any(Movimiento.class));

        ArgumentCaptor<AjusteStockDTO> captor = ArgumentCaptor.forClass(AjusteStockDTO.class);
        verify(inventarioClient).ajustarStock(captor.capture());
        assertEquals(1L, captor.getValue().getProductoId());
        assertEquals(2L, captor.getValue().getBodegaId());
        assertEquals(50, captor.getValue().getDelta());
    }

    // ---- registrar() - SALIDA ----

    @Test
    void registrar_salida_deberiaGuardarYAjustarStockNegativoEnOrigen() {
        MovimientoRequestDTO request = requestDePrueba(TipoMovimiento.SALIDA, 1L, null);
        Movimiento guardado = movimientoDePrueba(2L, TipoMovimiento.SALIDA, 1L, null);
        doNothing().when(movimientoValidator).validarCamposBodega(any(), any(), any());
        when(repository.save(any(Movimiento.class))).thenReturn(guardado);

        MovimientoResponseDTO resultado = service.registrar(request);

        assertNotNull(resultado);
        assertEquals(TipoMovimiento.SALIDA, resultado.getTipo());
        verify(movimientoValidator).validarCamposBodega(TipoMovimiento.SALIDA, 1L, null);

        ArgumentCaptor<AjusteStockDTO> captor = ArgumentCaptor.forClass(AjusteStockDTO.class);
        verify(inventarioClient).ajustarStock(captor.capture());
        assertEquals(-50, captor.getValue().getDelta());
        assertEquals(1L, captor.getValue().getBodegaId());
    }

    // ---- registrar() - TRANSFERENCIA ----

    @Test
    void registrar_transferencia_deberiaAjustarStockEnOrigenYDestino() {
        MovimientoRequestDTO request = requestDePrueba(TipoMovimiento.TRANSFERENCIA, 1L, 2L);
        Movimiento guardado = movimientoDePrueba(3L, TipoMovimiento.TRANSFERENCIA, 1L, 2L);
        doNothing().when(movimientoValidator).validarCamposBodega(any(), any(), any());
        when(repository.save(any(Movimiento.class))).thenReturn(guardado);

        MovimientoResponseDTO resultado = service.registrar(request);

        assertNotNull(resultado);
        assertEquals(TipoMovimiento.TRANSFERENCIA, resultado.getTipo());
        verify(movimientoValidator).validarCamposBodega(TipoMovimiento.TRANSFERENCIA, 1L, 2L);

        ArgumentCaptor<AjusteStockDTO> captor = ArgumentCaptor.forClass(AjusteStockDTO.class);
        verify(inventarioClient, times(2)).ajustarStock(captor.capture());

        List<AjusteStockDTO> ajustes = captor.getAllValues();
        // Primer ajuste: descuento en bodega origen
        assertEquals(-50, ajustes.get(0).getDelta());
        assertEquals(1L, ajustes.get(0).getBodegaId());
        // Segundo ajuste: suma en bodega destino
        assertEquals(50, ajustes.get(1).getDelta());
        assertEquals(2L, ajustes.get(1).getBodegaId());
    }

    @Test
    void registrar_transferenciaFallaEnDestino_deberiaRevertirYLanzarExcepcion() {
        MovimientoRequestDTO request = requestDePrueba(TipoMovimiento.TRANSFERENCIA, 1L, 2L);
        Movimiento guardado = movimientoDePrueba(3L, TipoMovimiento.TRANSFERENCIA, 1L, 2L);

        // Origen OK
        doNothing().when(movimientoValidator).validarCamposBodega(any(), any(), any());
        when(repository.save(any(Movimiento.class))).thenReturn(guardado);

        // Destino Falla
        doAnswer(invocation -> {

            AjusteStockDTO dto = invocation.getArgument(0);

            // ORIGEN (bodega 1, negativo)
            if (dto.getBodegaId().equals(1L) && dto.getDelta() < 0) {
                return null;
            }

            // DESTINO (bodega 2, positivo) → FALLA
            if (dto.getBodegaId().equals(2L) && dto.getDelta() > 0) {
                throw new RuntimeException("Error al ajustar destino");
            }

            // ROLLBACK (bodega 1, positivo)
            if (dto.getBodegaId().equals(1L) && dto.getDelta() > 0) {
                return null;
            }

            return null;

        }).when(inventarioClient).ajustarStock(any(AjusteStockDTO.class));
        ValidacionMovimientoException ex = assertThrows(
                ValidacionMovimientoException.class,
                () -> service.registrar(request));

        assertTrue(ex.getMessage().toLowerCase().contains("transferencia"));
        verify(inventarioClient, times(3)).ajustarStock(any(AjusteStockDTO.class));
    }

    // ---- registrar() - validación falla antes de guardar ----

    @Test
    void registrar_validacionFalla_deberiaLanzarExcepcionSinGuardarNiAjustar() {
        MovimientoRequestDTO request = requestDePrueba(TipoMovimiento.ENTRADA, null, null);
        doThrow(new ValidacionMovimientoException("El tipo ENTRADA requiere bodegaDestinoId."))
                .when(movimientoValidator).validarCamposBodega(any(), any(), any());

        assertThrows(ValidacionMovimientoException.class, () -> service.registrar(request));

        verify(repository, never()).save(any());
        verify(inventarioClient, never()).ajustarStock(any());
    }

    // ---- obtenerTodos() ----

    @Test
    void obtenerTodos_sinMovimientos_deberiaRetornarListaVacia() {
        when(repository.findAll()).thenReturn(List.of());

        List<MovimientoResponseDTO> resultado = service.obtenerTodos();

        assertTrue(resultado.isEmpty());
        verify(repository).findAll();
    }

    @Test
    void obtenerTodos_conMovimientos_deberiaRetornarLista() {
        Movimiento m1 = movimientoDePrueba(1L, TipoMovimiento.ENTRADA, null, 1L);
        Movimiento m2 = movimientoDePrueba(2L, TipoMovimiento.SALIDA, 1L, null);
        when(repository.findAll()).thenReturn(List.of(m1, m2));

        List<MovimientoResponseDTO> resultado = service.obtenerTodos();

        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(TipoMovimiento.ENTRADA, resultado.get(0).getTipo());
        assertEquals(TipoMovimiento.SALIDA, resultado.get(1).getTipo());
        verify(repository).findAll();
    }

    // ---- obtenerPorId() ----

    @Test
    void obtenerPorId_existente_deberiaRetornarMovimiento() {
        Movimiento m = movimientoDePrueba(1L, TipoMovimiento.ENTRADA, null, 2L);
        when(repository.findById(1L)).thenReturn(Optional.of(m));

        MovimientoResponseDTO resultado = service.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(TipoMovimiento.ENTRADA, resultado.getTipo());
        assertEquals(50, resultado.getCantidad());
        assertEquals("Motivo de prueba", resultado.getMotivo());
        verify(repository).findById(1L);
    }

    @Test
    void obtenerPorId_inexistente_deberiaLanzarExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        MovimientoNoEncontradoException ex = assertThrows(
                MovimientoNoEncontradoException.class,
                () -> service.obtenerPorId(99L));

        assertTrue(ex.getMessage().contains("99"));
        verify(repository).findById(99L);
    }

    // ---- obtenerPorProducto() ----

    @Test
    void obtenerPorProducto_conMovimientos_deberiaRetornarLista() {
        Movimiento m = movimientoDePrueba(1L, TipoMovimiento.ENTRADA, null, 1L);
        when(repository.findByProductoId(1L)).thenReturn(List.of(m));

        List<MovimientoResponseDTO> resultado = service.obtenerPorProducto(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getProductoId());
        verify(repository).findByProductoId(1L);
    }

    @Test
    void obtenerPorProducto_sinMovimientos_deberiaRetornarListaVacia() {
        when(repository.findByProductoId(99L)).thenReturn(List.of());

        List<MovimientoResponseDTO> resultado = service.obtenerPorProducto(99L);

        assertTrue(resultado.isEmpty());
        verify(repository).findByProductoId(99L);
    }

    // ---- obtenerPorBodega() ----

    @Test
    void obtenerPorBodega_conMovimientos_deberiaRetornarLista() {
        Movimiento m = movimientoDePrueba(1L, TipoMovimiento.ENTRADA, null, 1L);
        when(repository.findByBodegaOrigenIdOrBodegaDestinoId(1L, 1L)).thenReturn(List.of(m));

        List<MovimientoResponseDTO> resultado = service.obtenerPorBodega(1L);

        assertEquals(1, resultado.size());
        verify(repository).findByBodegaOrigenIdOrBodegaDestinoId(1L, 1L);
    }

    @Test
    void obtenerPorBodega_sinMovimientos_deberiaRetornarListaVacia() {
        when(repository.findByBodegaOrigenIdOrBodegaDestinoId(99L, 99L)).thenReturn(List.of());

        List<MovimientoResponseDTO> resultado = service.obtenerPorBodega(99L);

        assertTrue(resultado.isEmpty());
        verify(repository).findByBodegaOrigenIdOrBodegaDestinoId(99L, 99L);
    }

    // ---- obtenerPorTipo() ----

    @Test
    void obtenerPorTipo_ENTRADA_deberiaRetornarMovimientosEntrada() {
        Movimiento m = movimientoDePrueba(1L, TipoMovimiento.ENTRADA, null, 2L);
        when(repository.findByTipo(TipoMovimiento.ENTRADA)).thenReturn(List.of(m));

        List<MovimientoResponseDTO> resultado = service.obtenerPorTipo(TipoMovimiento.ENTRADA);

        assertEquals(1, resultado.size());
        assertEquals(TipoMovimiento.ENTRADA, resultado.get(0).getTipo());
        verify(repository).findByTipo(TipoMovimiento.ENTRADA);
    }

    @Test
    void obtenerPorTipo_SALIDA_sinMovimientos_deberiaRetornarListaVacia() {
        when(repository.findByTipo(TipoMovimiento.SALIDA)).thenReturn(List.of());

        List<MovimientoResponseDTO> resultado = service.obtenerPorTipo(TipoMovimiento.SALIDA);

        assertTrue(resultado.isEmpty());
        verify(repository).findByTipo(TipoMovimiento.SALIDA);
    }

    @Test
    void obtenerPorTipo_TRANSFERENCIA_deberiaRetornarMovimientosTransferencia() {
        Movimiento m = movimientoDePrueba(3L, TipoMovimiento.TRANSFERENCIA, 1L, 2L);
        when(repository.findByTipo(TipoMovimiento.TRANSFERENCIA)).thenReturn(List.of(m));

        List<MovimientoResponseDTO> resultado = service.obtenerPorTipo(TipoMovimiento.TRANSFERENCIA);

        assertEquals(1, resultado.size());
        assertEquals(TipoMovimiento.TRANSFERENCIA, resultado.get(0).getTipo());
        verify(repository).findByTipo(TipoMovimiento.TRANSFERENCIA);
    }
}
