package com.microservice.inventario_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.microservice.inventario_service.feignclient.FeignSupport.BodegaExternaException;
import com.microservice.inventario_service.feignclient.FeignSupport.ProductoExternoException;

import lombok.AllArgsConstructor;
import lombok.Data;

@RestControllerAdvice
public class ManejadorGlobal {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("Argumento inválido", e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        String mensaje = e.getMessage();
        // Fallback para otros RuntimeException no manejados específicamente
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("Error interno del servidor", mensaje));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error interno del servidor", e.getMessage()));
    }

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private String error;
        private String mensaje;
    }

    // Excepciones de dominio anidadas
    public static class InventarioNoEncontradoException extends RuntimeException {
        public InventarioNoEncontradoException(String message) {
            super(message);
        }

        public InventarioNoEncontradoException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class StockInsuficienteException extends RuntimeException {
        public StockInsuficienteException(String message) {
            super(message);
        }

        public StockInsuficienteException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class InventarioPersistenciaException extends RuntimeException {
        public InventarioPersistenciaException(String message) {
            super(message);
        }

        public InventarioPersistenciaException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // Handlers específicos para las excepciones de dominio
    @ExceptionHandler(InventarioNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(InventarioNoEncontradoException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Inventario no encontrado", e.getMessage()));
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ErrorResponse> handleStockInsuficiente(StockInsuficienteException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Stock insuficiente", e.getMessage()));
    }

    @ExceptionHandler(InventarioPersistenciaException.class)
    public ResponseEntity<ErrorResponse> handlePersistencia(InventarioPersistenciaException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error de persistencia", e.getMessage()));
    }

    @ExceptionHandler(ProductoExternoException.class)
    public ResponseEntity<ErrorResponse> handleProductoExterno(ProductoExternoException e) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse("Servicio de productos no disponible", e.getMessage()));
    }

    @ExceptionHandler(BodegaExternaException.class)
    public ResponseEntity<ErrorResponse> handleBodegaExterna(BodegaExternaException e) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse("Servicio de bodegas no disponible", e.getMessage()));
    }

}
