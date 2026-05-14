package com.microservice.inventario_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.inventario_service.dto.AjusteStockRequestDTO;
import com.microservice.inventario_service.model.Inventario;
import com.microservice.inventario_service.service.InventarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventarios")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @PostMapping
    public ResponseEntity<Inventario> crearInventario(@RequestBody Inventario inventario) {
        Inventario nuevoInventario = inventarioService.crearInventario(inventario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoInventario);
    }

    @GetMapping
    public ResponseEntity<List<Inventario>> obtenerTodosInventarios() {
        List<Inventario> inventarios = inventarioService.obtenerTodosInventarios();
        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventario> obtenerInventarioPorId(@PathVariable Long id) {
        Inventario inventario = inventarioService.obtenerInventarioPorId(id);
        return ResponseEntity.ok(inventario);
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<Inventario>> obtenerInventariosPorProducto(@PathVariable Long productoId) {
        List<Inventario> inventarios = inventarioService.obtenerInventariosPorProducto(productoId);
        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/bodega/{bodegaId}")
    public ResponseEntity<List<Inventario>> obtenerInventariosPorBodega(@PathVariable Long bodegaId) {
        List<Inventario> inventarios = inventarioService.obtenerInventariosPorBodega(bodegaId);
        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/{id}/necesita-reposicion")
    public ResponseEntity<Boolean> necesitaReposicion(@PathVariable Long id) {
        boolean necesita = inventarioService.necesitaReposicion(id);
        return ResponseEntity.ok(necesita);
    }

    @PatchMapping("/ajustar")
    public ResponseEntity<Inventario> ajustarStock(@RequestBody AjusteStockRequestDTO dto) {
        Inventario inventario = inventarioService.ajustarStock(dto.getProductoId(), dto.getBodegaId(), dto.getDelta());
        return ResponseEntity.ok(inventario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarInventario(@PathVariable Long id) {
        inventarioService.eliminarInventario(id);
        return ResponseEntity.ok("Inventario eliminado exitosamente");
    }

}
