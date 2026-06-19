package com.microservice.movimiento_service.validation;

import org.springframework.stereotype.Component;
import com.microservice.movimiento_service.exception.ManejadorGlobal.ValidacionMovimientoException;
import com.microservice.movimiento_service.model.TipoMovimiento;

@Component
public class MovimientoValidator {

    public void validarCamposBodega(TipoMovimiento tipo, Long origenId, Long destinoId) {
        switch (tipo) {
            case ENTRADA:
                if (destinoId == null) {
                    throw new ValidacionMovimientoException("El tipo ENTRADA requiere bodegaDestinoId.");
                }
                break;
            case SALIDA:
                if (origenId == null) {
                    throw new ValidacionMovimientoException("El tipo SALIDA requiere bodegaOrigenId.");
                }
                break;
            case TRANSFERENCIA:
                if (origenId == null || destinoId == null) {
                    throw new ValidacionMovimientoException("El tipo TRANSFERENCIA requiere bodegaOrigenId y bodegaDestinoId.");
                }
                // Simplificado: se permite que origen y destino sean la misma bodega para permitir reorganización interna
                break;
        }
    }
}
