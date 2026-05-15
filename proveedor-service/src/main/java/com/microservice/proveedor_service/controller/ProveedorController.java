package com.microservice.proveedor_service.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.proveedor_service.dto.ProveedorRequestDTO;
import com.microservice.proveedor_service.dto.ProveedorResponseDTO;
import com.microservice.proveedor_service.service.ProveedorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor

public class ProveedorController {

    private final ProveedorService proveedorService;

    @PostMapping
    public ResponseEntity<ProveedorResponseDTO> crearProveedor(@Valid @RequestBody ProveedorRequestDTO proveedorRequest) {
        ProveedorResponseDTO creado = proveedorService.crearProveedor(proveedorRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public ResponseEntity<List<ProveedorResponseDTO>> obtenerTodosProveedores() {
        List<ProveedorResponseDTO> proveedores = proveedorService.obtenerTodosProveedores();
        return ResponseEntity.ok(proveedores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorResponseDTO> obtenerProveedorPorId(@PathVariable Long id) {
        ProveedorResponseDTO dto = proveedorService.obtenerProveedorPorId(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/activos/listar")
    public ResponseEntity<List<ProveedorResponseDTO>> obtenerProveedoresActivos() {
        List<ProveedorResponseDTO> proveedores = proveedorService.obtenerProveedoresActivos();
        return ResponseEntity.ok(proveedores);
    }

    @GetMapping("/rut/{rut}")
    public ResponseEntity<ProveedorResponseDTO> obtenerProveedorPorRut(@PathVariable String rut) {
        Optional<ProveedorResponseDTO> proveedor = proveedorService.obtenerProveedorPorRut(rut);
        return proveedor.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorResponseDTO> actualizarProveedor(@PathVariable Long id,
                                                            @Valid @RequestBody ProveedorRequestDTO proveedorRequest) {
        ProveedorResponseDTO actualizado = proveedorService.actualizarProveedor(id, proveedorRequest);
        return ResponseEntity.ok(actualizado);
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<String> desactivarProveedor(@PathVariable Long id) {
        proveedorService.desactivarProveedor(id);
        return ResponseEntity.ok("Proveedor desactivado exitosamente");
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<String> activarProveedor(@PathVariable Long id) {
        proveedorService.activarProveedor(id);
        return ResponseEntity.ok("Proveedor activado exitosamente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable Long id) {
        proveedorService.eliminarProveedor(id);
        return ResponseEntity.noContent().build();
    }
}
