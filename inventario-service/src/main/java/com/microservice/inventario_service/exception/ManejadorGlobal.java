package com.microservice.inventario_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.AllArgsConstructor;
import lombok.Data;

@RestControllerAdvice
public class ManejadorGlobal {

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(org.springframework.http.converter.HttpMessageNotReadableException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Datos inválidos", "Error al procesar la solicitud: formato de datos inválido o valor no permitido."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Argumento inválido", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("La solicitud contiene datos inválidos");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Datos inválidos", mensaje));
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

    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(org.springframework.dao.DataAccessException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error de base de datos",
                        "No pudimos completar la operación en la base de datos. Por favor, intenta nuevamente."));
    }

    @ExceptionHandler(feign.FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(feign.FeignException e) {
        int status = e.status() > 0 ? e.status() : 503;
        return ResponseEntity
                .status(HttpStatus.valueOf(status))
                .body(new ErrorResponse("Error de comunicación externa",
                        "No se pudo completar la comunicación con el servicio externo. Detalles: " + e.getMessage()));
    }

}
