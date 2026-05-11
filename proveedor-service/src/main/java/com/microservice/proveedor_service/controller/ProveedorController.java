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

import com.microservice.proveedor_service.model.Proveedor;
import com.microservice.proveedor_service.service.ProveedorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor

public class ProveedorController {

    private final ProveedorService proveedorService;

    @PostMapping
    public ResponseEntity<Proveedor> crearProveedor(@RequestBody Proveedor proveedor) {
        Proveedor nuevoProveedor = proveedorService.crearProveedor(proveedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProveedor);
    }

    @GetMapping
    public ResponseEntity<List<Proveedor>> obtenerTodosProveedores() {
        List<Proveedor> proveedores = proveedorService.obtenerTodosProveedores();
        return ResponseEntity.ok(proveedores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> obtenerProveedorPorId(@PathVariable Long id) {
        Proveedor proveedor = proveedorService.obtenerProveedorPorId(id);
        return ResponseEntity.ok(proveedor);
    }

    @GetMapping("/activos/listar")
    public ResponseEntity<List<Proveedor>> obtenerProveedoresActivos() {
        List<Proveedor> proveedores = proveedorService.obtenerProveedoresActivos();
        return ResponseEntity.ok(proveedores);
    }

    @GetMapping("/rut/{rut}")
    public ResponseEntity<Proveedor> obtenerProveedorPorRut(@PathVariable String rut) {
        Optional<Proveedor> proveedor = proveedorService.obtenerProveedorPorRut(rut);
        return proveedor.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizarProveedor(@PathVariable Long id,
                                                         @RequestBody Proveedor proveedor) {
        Proveedor proveedorActualizado = proveedorService.actualizarProveedor(id, proveedor);
        return ResponseEntity.ok(proveedorActualizado);
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
    public ResponseEntity<String> eliminarProveedor(@PathVariable Long id) {
        proveedorService.eliminarProveedor(id);
        return ResponseEntity.ok("Proveedor eliminado exitosamente");
    }
}
