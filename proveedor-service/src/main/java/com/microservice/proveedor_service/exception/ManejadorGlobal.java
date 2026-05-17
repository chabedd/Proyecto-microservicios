package com.microservice.proveedor_service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.AllArgsConstructor;
import lombok.Data;

@RestControllerAdvice
public class ManejadorGlobal {

    private static final Logger log = LoggerFactory.getLogger(ManejadorGlobal.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Argumento inválido: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse("Argumento inválido", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("La solicitud contiene datos inválidos");
        log.warn("Validación fallida: {}", mensaje);
        ErrorResponse error = new ErrorResponse("Datos inválidos", mensaje);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ProveedorNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleProveedorNoEncontrado(ProveedorNoEncontradoException e) {
        log.warn("Proveedor no encontrado: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse("Recurso no encontrado", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(RutDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleRutDuplicado(RutDuplicadoException e) {
        log.warn("Conflicto de RUT: {}", e.getMessage());
        ErrorResponse error = new ErrorResponse("Conflicto de datos", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ProveedorPersistenciaException.class)
    public ResponseEntity<ErrorResponse> handleProveedorPersistencia(ProveedorPersistenciaException e) {
        log.error("Error de persistencia: {}", e.getMessage(), e);
        ErrorResponse error = new ErrorResponse("Error interno del servidor", "Ocurrió un problema al procesar la operación del proveedor");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        log.error("Error no controlado: {}", e.getMessage(), e);
        ErrorResponse error = new ErrorResponse("Error interno del servidor", "Ha ocurrido un error inesperado");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private String error;
        private String mensaje;
    }

    // Excepciones de dominio anidadas
    public static class ProveedorNoEncontradoException extends RuntimeException {
        public ProveedorNoEncontradoException(String message) {
            super(message);
        }

        public ProveedorNoEncontradoException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class RutDuplicadoException extends RuntimeException {
        public RutDuplicadoException(String message) {
            super(message);
        }

        public RutDuplicadoException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class ProveedorPersistenciaException extends RuntimeException {
        public ProveedorPersistenciaException(String message) {
            super(message);
        }

        public ProveedorPersistenciaException(String message, Throwable cause) {
            super(message, cause);
        }
    }



}
