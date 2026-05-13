package com.microservice.producto_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.producto_service.dto.ProductoRequestDTO;
import com.microservice.producto_service.dto.ProductoResponseDTO;
import com.microservice.producto_service.service.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/productos") 
@RequiredArgsConstructor

public class ProductoController {
    private final ProductoService service;
    @PostMapping 
    public ResponseEntity<ProductoResponseDTO> crear(@Valid @RequestBody ProductoRequestDTO dto) { 
        return ResponseEntity.ok(service.crear(dto)); }
    @GetMapping 
    public ResponseEntity<List<ProductoResponseDTO>> obtenerTodos() { 
        return ResponseEntity.ok(service.obtenerTodos()); }
    @GetMapping("/{id}") 
    public ResponseEntity<ProductoResponseDTO> obtener(@PathVariable Long id) { 
        return ResponseEntity.ok(service.obtenerPorId(id)); }
    @PutMapping("/{id}") 
    public ResponseEntity<ProductoResponseDTO> actualizar(@PathVariable Long id,@Valid @RequestBody ProductoRequestDTO dto) { 
        return ResponseEntity.ok(service.actualizar(id, dto)); }
    @DeleteMapping("/{id}") 
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { 
        service.eliminar(id); 
        return ResponseEntity.noContent().build();
     }
    
}
