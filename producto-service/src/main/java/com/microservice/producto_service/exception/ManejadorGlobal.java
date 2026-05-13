package com.microservice.producto_service.exception;

import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.Data;
@Data
@RestControllerAdvice
public class ManejadorGlobal {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRecursoNoEncontrado(RuntimeException e) {
        ErrorResponse error = new ErrorResponse("Recurso no encontrado", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    @ExceptionHandler(SQLException.class)
    Public ResponseEntity<ErrorResponse> handlerSqlException(SQLException s){
        ErrorResponse error = new ErrorResponse(error:"Error en la base de datos", e.getMessage());
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(error)
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        ErrorResponse error = new ErrorResponse("Error interno del servidor", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    public class ErrorResponse {
        private String error;
        private String mensaje;

       

}