package com.microservice.movimiento_service.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@RestControllerAdvice
public class ManejadorGlobal {

    public static class MovimientoNoEncontradoException extends RuntimeException {
        public MovimientoNoEncontradoException(String mensaje) {
            super(mensaje);
        }
    }

    public static class ValidacionMovimientoException extends RuntimeException {
        public ValidacionMovimientoException(String mensaje) {
            super(mensaje);
        }
    }

    @ExceptionHandler(MovimientoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(MovimientoNoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Recurso no encontrado", e.getMessage()));
    }

    @ExceptionHandler(ValidacionMovimientoException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(ValidacionMovimientoException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Error de validación", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String errores = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Error de validación", errores));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String mensaje = "Parámetro inválido: " + e.getValue();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("Error de tipo de parámetro", mensaje));
    }

    @ExceptionHandler(feign.FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(feign.FeignException e) {
        String mensaje = e.contentUTF8();
        if (mensaje != null && !mensaje.isBlank()) {
            try {
                int index = mensaje.indexOf("\"mensaje\":\"");
                if (index != -1) {
                    int start = index + 11;
                    int end = mensaje.indexOf("\"", start);
                    if (end != -1) {
                        mensaje = mensaje.substring(start, end);
                    }
                }
            } catch (Exception ex) {
                // fall back to contentUTF8
            }
        } else {
            mensaje = "Error de comunicación entre servicios internos.";
        }

        HttpStatus status = HttpStatus.resolve(e.status());
        if (status == null) {
            status = HttpStatus.BAD_GATEWAY;
        }
        return ResponseEntity.status(status)
                .body(new ErrorResponse("Error en servicio externo", mensaje));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
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
