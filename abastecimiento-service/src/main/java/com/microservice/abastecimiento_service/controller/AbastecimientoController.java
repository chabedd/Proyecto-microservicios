package com.microservice.abastecimiento_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.abastecimiento_service.dto.OrdenCompraRequestDTO;
import com.microservice.abastecimiento_service.dto.OrdenCompraResponseDTO;
import com.microservice.abastecimiento_service.service.AbastecimientoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/abastecimiento") 
@RequiredArgsConstructor public class AbastecimientoController {
    private final AbastecimientoService service;
    @PostMapping 
    public ResponseEntity<OrdenCompraResponseDTO> crear(@Valid @RequestBody OrdenCompraRequestDTO dto) { 
        return ResponseEntity.ok(service.crearOrden(dto)); }
    @PostMapping("/orden-automatica") 
    public ResponseEntity<Void> generarOrdenAutomatica(@RequestParam Long productoId) { 
        service.generarOrdenAutomatica(productoId); 
        return ResponseEntity.ok().build(); }
    @GetMapping public ResponseEntity<List<OrdenCompraResponseDTO>> obtenerTodas() { 
        return ResponseEntity.ok(service.obtenerTodas()); }
    @GetMapping("/{id}") public ResponseEntity<OrdenCompraResponseDTO> obtener(@PathVariable Long id) { 
        return ResponseEntity.ok(service.obtenerPorId(id)); }
    @PutMapping("/{id}/estado") public ResponseEntity<OrdenCompraResponseDTO> cambiarEstado(@PathVariable Long id, @RequestParam String estado) { 
        return ResponseEntity.ok(service.cambiarEstado(id, estado)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> cancelarOrden(@PathVariable Long id) { 
        service.cancelarOrden(id); 
        return ResponseEntity.noContent().build(); }
}


