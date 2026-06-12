package com.microservice.inventario_service.validation;

import org.springframework.stereotype.Component;
import com.microservice.inventario_service.feignclient.FeignSupport.BodegaDTO;

@Component
public class InventarioValidator {

    public void validarStockNoNegativo(int stockActual) {
        if (stockActual < 0) {
            throw new IllegalArgumentException("El stock actual no puede ser menor a cero.");
        }
    }

    public void validarBodegaActiva(BodegaDTO bodega, Long bodegaId) {
        if (bodega == null) {
            throw new IllegalArgumentException("La bodega con id " + bodegaId + " no existe.");
        }
    }

    public void validarRegistroDuplicado(boolean existe) {
        if (existe) {
            throw new IllegalArgumentException("Ya existe un registro de inventario para este producto y bodega.");
        }
    }
}
