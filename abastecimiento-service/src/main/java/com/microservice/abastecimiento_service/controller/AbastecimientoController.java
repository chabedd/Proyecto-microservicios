package com.microservice.abastecimiento_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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
@RequiredArgsConstructor
public class AbastecimientoController {

    private final AbastecimientoService service;

    @PostMapping
    public ResponseEntity<OrdenCompraResponseDTO> crear(@Valid @RequestBody OrdenCompraRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearOrden(dto));
    }

    @GetMapping
    public ResponseEntity<List<OrdenCompraResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenCompraResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    // bodegaId requerido solo cuando estado=RECIBIDA para registrar la ENTRADA en inventario
    @PutMapping("/{id}/estado")
    public ResponseEntity<OrdenCompraResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado,
            @RequestParam(required = false) Long bodegaId) {
        return ResponseEntity.ok(service.cambiarEstado(id, estado, bodegaId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarOrden(@PathVariable Long id) {
        service.cancelarOrden(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint consultado por ms-proveedores antes de eliminar un proveedor
    @GetMapping("/proveedor/{proveedorId}/tiene-ordenes-activas")
    public ResponseEntity<Boolean> tieneOrdenesActivas(@PathVariable Long proveedorId) {
        return ResponseEntity.ok(service.tieneOrdenesActivasPorProveedor(proveedorId));
    }
}
