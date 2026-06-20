package com.microservice.producto_service.exception;

import java.sql.SQLException;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class ManejadorGlobal {

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(org.springframework.http.converter.HttpMessageNotReadableException e) {
        log.warn("Error al leer mensaje HTTP: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Datos inválidos", "Error al procesar la solicitud: formato de datos inválido o valor no permitido."));
    }

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleProductoNoEncontrado(ProductoNoEncontradoException e) {
        log.warn("Producto no encontrado: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Producto no encontrado", e.getMessage()));
    }

    @ExceptionHandler(SkuDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleSkuDuplicado(SkuDuplicadoException e) {
        log.warn("Conflicto de SKU: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("SKU duplicado", e.getMessage()));
    }

    @ExceptionHandler(ProductoConStockException.class)
    public ResponseEntity<ErrorResponse> handleProductoConStock(ProductoConStockException e) {
        log.warn("Intento de desactivar producto con stock: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("Operación no permitida", e.getMessage()));
    }

    @ExceptionHandler(ProductoValidacionException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(ProductoValidacionException e) {
        log.warn("Error de validación de negocio: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Error de validación", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String errores = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("Validación de entrada fallida: {}", errores);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Datos de entrada inválidos", errores));
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> handleSqlException(SQLException e) {
        log.error("Error de base de datos: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error de base de datos", "Ocurrió un problema al acceder a los datos"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Error no controlado: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error interno del servidor", "Ha ocurrido un error inesperado"));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorResponse {
        private String error;
        private String mensaje;
    }

    public static class ProductoNoEncontradoException extends RuntimeException {
        public ProductoNoEncontradoException(String message) {
            super(message);
        }
    }

    public static class SkuDuplicadoException extends RuntimeException {
        public SkuDuplicadoException(String message) {
            super(message);
        }
    }

    public static class ProductoConStockException extends RuntimeException {
        public ProductoConStockException(String message) {
            super(message);
        }
    }

    public static class ProductoValidacionException extends RuntimeException {
        public ProductoValidacionException(String message) {
            super(message);
        }
    }

}
