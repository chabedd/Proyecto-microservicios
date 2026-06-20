package com.microservice.producto_service.validation;

import org.springframework.stereotype.Component;
import com.microservice.producto_service.exception.ManejadorGlobal.SkuDuplicadoException;
import com.microservice.producto_service.exception.ManejadorGlobal.ProductoValidacionException;

@Component
public class ProductoValidator {

    public void validarCodigoDuplicado(boolean existe, String codigo) {
        if (existe) {
            throw new SkuDuplicadoException("Ya existe un producto con el SKU: " + codigo);
        }
    }

    public void validarPrecioBase(Double precioBase) {
        if (precioBase == null || precioBase <= 0) {
            throw new ProductoValidacionException("El precio base debe ser mayor a 0.");
        }
    }
}
