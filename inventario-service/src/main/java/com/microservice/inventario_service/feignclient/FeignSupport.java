package com.microservice.inventario_service.feignclient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public final class FeignSupport {

    private FeignSupport() {
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductoDTO {
        private Long id;
        private String codigo;
        private String nombre;
        private Double precioBase;
        private Long proveedorId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BodegaDTO {
        private Long id;
        private String nombre;
        private String ubicacion;
        private Long capacidad;
    }
}