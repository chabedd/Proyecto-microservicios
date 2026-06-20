package com.microservice.bodega_service.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@RestControllerAdvice
public class ManejadorGlobal {

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(org.springframework.http.converter.HttpMessageNotReadableException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Datos inválidos", "Error al procesar la solicitud: formato de datos inválido o valor no permitido."));
    }

    public static class BodegaNotFoundException extends RuntimeException {
        public BodegaNotFoundException(String mensaje) {
            super(mensaje);
        }
    }

    public static class BodegaNombreDuplicadoException extends RuntimeException {
        public BodegaNombreDuplicadoException(String mensaje) {
            super(mensaje);
        }
    }

    @ExceptionHandler(BodegaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(BodegaNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Bodega no encontrada", e.getMessage()));
    }

    @ExceptionHandler(BodegaNombreDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleNombreDuplicado(BodegaNombreDuplicadoException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("Nombre duplicado", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(MethodArgumentNotValidException e) {
        String errores = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Error de validación", errores));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error interno del servidor", e.getMessage()));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorResponse {
        private String error;
        private String mensaje;
    }
}
