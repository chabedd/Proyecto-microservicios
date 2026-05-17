package com.microservice.inventario_service.feignclient;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
        private Boolean activa;
    }

    public static class ProductoExternoException extends RuntimeException {
        public ProductoExternoException(String message) {
            super(message);
        }

        public ProductoExternoException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class BodegaExternaException extends RuntimeException {
        public BodegaExternaException(String message) {
            super(message);
        }

        public BodegaExternaException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @Component
    @Slf4j
    public static class ProductoClientFallback implements ProductoClient {

        @Override
        public ProductoDTO obtenerProductoPorId(Long id) {
            log.error("Error al conectar con ms-producto para obtener producto id={}", id);
            throw new ProductoExternoException("No se pudo verificar la existencia del producto en ms-producto. Intenta nuevamente.");
        }
    }

    @Component
    @Slf4j
    public static class BodegaClientFallback implements BodegaClient {

        @Override
        public BodegaDTO obtenerBodegaPorId(Long id) {
            log.error("Error al conectar con ms-bodega para obtener bodega id={}", id);
            throw new BodegaExternaException("No se pudo verificar la existencia de la bodega en ms-bodega. Intenta nuevamente.");
        }
    }
}