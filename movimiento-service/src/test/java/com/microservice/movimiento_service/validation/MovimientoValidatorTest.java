package com.microservice.movimiento_service.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.microservice.movimiento_service.exception.ManejadorGlobal.ValidacionMovimientoException;
import com.microservice.movimiento_service.model.TipoMovimiento;

@ExtendWith(MockitoExtension.class)
class MovimientoValidatorTest {

    private MovimientoValidator validator;

    @BeforeEach
    void setUp() {
        validator = new MovimientoValidator();
    }

    // ---- ENTRADA ----

    @Test
    void validarCamposBodega_ENTRADA_sinDestino_deberiaLanzarExcepcion() {
        ValidacionMovimientoException ex = assertThrows(
                ValidacionMovimientoException.class,
                () -> validator.validarCamposBodega(TipoMovimiento.ENTRADA, null, null)
        );
        assertTrue(ex.getMessage().contains("ENTRADA"));
        assertTrue(ex.getMessage().contains("bodegaDestinoId"));
    }

    @Test
    void validarCamposBodega_ENTRADA_conDestino_deberiaNoLanzarExcepcion() {
        assertDoesNotThrow(
                () -> validator.validarCamposBodega(TipoMovimiento.ENTRADA, null, 2L)
        );
    }

    @Test
    void validarCamposBodega_ENTRADA_conOrigenYDestino_deberiaNoLanzarExcepcion() {
        assertDoesNotThrow(
                () -> validator.validarCamposBodega(TipoMovimiento.ENTRADA, 1L, 2L)
        );
    }

    // ---- SALIDA ----

    @Test
    void validarCamposBodega_SALIDA_sinOrigen_deberiaLanzarExcepcion() {
        ValidacionMovimientoException ex = assertThrows(
                ValidacionMovimientoException.class,
                () -> validator.validarCamposBodega(TipoMovimiento.SALIDA, null, null)
        );
        assertTrue(ex.getMessage().contains("SALIDA"));
        assertTrue(ex.getMessage().contains("bodegaOrigenId"));
    }

    @Test
    void validarCamposBodega_SALIDA_conOrigen_deberiaNoLanzarExcepcion() {
        assertDoesNotThrow(
                () -> validator.validarCamposBodega(TipoMovimiento.SALIDA, 1L, null)
        );
    }

    @Test
    void validarCamposBodega_SALIDA_conOrigenYDestino_deberiaNoLanzarExcepcion() {
        assertDoesNotThrow(
                () -> validator.validarCamposBodega(TipoMovimiento.SALIDA, 1L, 2L)
        );
    }

    // ---- TRANSFERENCIA ----

    @Test
    void validarCamposBodega_TRANSFERENCIA_sinOrigen_deberiaLanzarExcepcion() {
        ValidacionMovimientoException ex = assertThrows(
                ValidacionMovimientoException.class,
                () -> validator.validarCamposBodega(TipoMovimiento.TRANSFERENCIA, null, 2L)
        );
        assertTrue(ex.getMessage().contains("TRANSFERENCIA"));
    }

    @Test
    void validarCamposBodega_TRANSFERENCIA_sinDestino_deberiaLanzarExcepcion() {
        ValidacionMovimientoException ex = assertThrows(
                ValidacionMovimientoException.class,
                () -> validator.validarCamposBodega(TipoMovimiento.TRANSFERENCIA, 1L, null)
        );
        assertTrue(ex.getMessage().contains("TRANSFERENCIA"));
    }

    @Test
    void validarCamposBodega_TRANSFERENCIA_sinNinguno_deberiaLanzarExcepcion() {
        assertThrows(
                ValidacionMovimientoException.class,
                () -> validator.validarCamposBodega(TipoMovimiento.TRANSFERENCIA, null, null)
        );
    }

    @Test
    void validarCamposBodega_TRANSFERENCIA_conAmbosIds_deberiaNoLanzarExcepcion() {
        assertDoesNotThrow(
                () -> validator.validarCamposBodega(TipoMovimiento.TRANSFERENCIA, 1L, 2L)
        );
    }

    @Test
    void validarCamposBodega_TRANSFERENCIA_mismoOrigenYDestino_deberiaNoLanzarExcepcion() {
        assertDoesNotThrow(
                () -> validator.validarCamposBodega(TipoMovimiento.TRANSFERENCIA, 1L, 1L)
        );
    }
}
