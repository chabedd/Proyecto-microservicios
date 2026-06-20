package com.microservice.abastecimiento_service.validation;

import org.springframework.stereotype.Component;
import com.microservice.abastecimiento_service.feignclient.ProveedorResponseDTO;
import com.microservice.abastecimiento_service.dto.ProductoResponseDTO;
import com.microservice.abastecimiento_service.model.TipoEstado;
import com.microservice.abastecimiento_service.exception.ManejadorGlobal.ReglaDeNegocioException;
import com.microservice.abastecimiento_service.exception.ManejadorGlobal.ValidacionException;

@Component
public class AbastecimientoValidator {

    public void validarProveedorActivo(ProveedorResponseDTO proveedor, Long proveedorId) {
        // Simplificado: se permite la creación de OC independientemente de si el
        // proveedor está activo o no
    }

    public void validarProductoActivo(ProductoResponseDTO producto, Long productoId) {
        // Simplificado: se permite incluir el producto en la OC independientemente de
        // si está activo o no
    }

    public void validarTransicion(TipoEstado actual, TipoEstado nuevo) {
        if (actual == null || nuevo == null) {
            throw new ValidacionException("Los estados actual y nuevo no pueden ser nulos.");
        }
        boolean valida = switch (actual) {
            case PENDIENTE -> nuevo == TipoEstado.APROBADA || nuevo == TipoEstado.CANCELADA;
            case APROBADA ->
                nuevo == TipoEstado.RECIBIDA || nuevo == TipoEstado.CANCELADA || nuevo == TipoEstado.PENDIENTE;
            case CANCELADA -> nuevo == TipoEstado.PENDIENTE;
            case RECIBIDA -> false;
        };
        if (!valida) {
            throw new ReglaDeNegocioException(
                    "Transición inválida: no se puede pasar de " + actual + " a " + nuevo + ".");
        }
    }

    public void validarBodegaParaRecepcion(Long bodegaId) {
        if (bodegaId == null) {
            throw new ValidacionException("Se requiere el parámetro bodegaId para recibir una OC.");
        }
    }
}
