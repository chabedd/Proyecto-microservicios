package com.microservice.inventario_service.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import com.microservice.inventario_service.feignclient.FeignSupport.BodegaDTO;

class InventarioValidatorTest {

    private final InventarioValidator validator = new InventarioValidator();

    @Test
    void validarStockNoNegativo_ValoresValidos_NoLanzaExcepcion() {
        assertDoesNotThrow(() -> validator.validarStockNoNegativo(0));
        assertDoesNotThrow(() -> validator.validarStockNoNegativo(100));
    }

    @Test
    void validarStockNoNegativo_ValorNegativo_LanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validarStockNoNegativo(-1));
        assertEquals("El stock actual no puede ser menor a cero.", ex.getMessage());
    }

    @Test
    void validarBodegaActiva_BodegaNula_LanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validarBodegaActiva(null, 1L));
        assertEquals("La bodega con id 1 no existe.", ex.getMessage());
    }

    @Test
    void validarBodegaActiva_BodegaValida_NoLanzaExcepcion() {
        BodegaDTO bodega = new BodegaDTO();
        assertDoesNotThrow(() -> validator.validarBodegaActiva(bodega, 1L));
    }

    @Test
    void validarRegistroDuplicado_Existe_LanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> validator.validarRegistroDuplicado(true));
        assertEquals("Ya existe un registro de inventario para este producto y bodega.", ex.getMessage());
    }

    @Test
    void validarRegistroDuplicado_NoExiste_NoLanzaExcepcion() {
        assertDoesNotThrow(() -> validator.validarRegistroDuplicado(false));
    }
}
