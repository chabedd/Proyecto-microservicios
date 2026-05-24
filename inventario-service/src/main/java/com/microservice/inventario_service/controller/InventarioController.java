package com.microservice.inventario_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.inventario_service.dto.AjusteStockRequestDto;
import com.microservice.inventario_service.dto.InventarioRequestDTO;
import com.microservice.inventario_service.dto.InventarioResponseDTO;
import com.microservice.inventario_service.service.InventarioService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventarios")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @PostMapping
    public ResponseEntity<InventarioResponseDTO> crearInventario(@Valid @RequestBody InventarioRequestDTO requestDTO) {
        InventarioResponseDTO responseDTO = inventarioService.crearInventario(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<InventarioResponseDTO>> obtenerTodosInventarios() {
        List<InventarioResponseDTO> inventarios = inventarioService.obtenerTodosInventarios();
        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioResponseDTO> obtenerInventarioPorId(@PathVariable Long id) {
        InventarioResponseDTO inventario = inventarioService.obtenerInventarioPorId(id);
        return ResponseEntity.ok(inventario);
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<InventarioResponseDTO>> obtenerInventariosPorProducto(@PathVariable Long productoId) {
        List<InventarioResponseDTO> inventarios = inventarioService.obtenerInventariosPorProducto(productoId);
        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/bodega/{bodegaId}")
    public ResponseEntity<List<InventarioResponseDTO>> obtenerInventariosPorBodega(@PathVariable Long bodegaId) {
        List<InventarioResponseDTO> inventarios = inventarioService.obtenerInventariosPorBodega(bodegaId);
        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/{id}/necesita-reposicion")
    public ResponseEntity<Boolean> necesitaReposicion(@PathVariable Long id) {
        boolean necesita = inventarioService.necesitaReposicion(id);
        return ResponseEntity.ok(necesita);
    }

    @PostMapping("/ajustar")
    public ResponseEntity<InventarioResponseDTO> ajustarStockPost(@Valid @RequestBody AjusteStockRequestDto dto) {
        InventarioResponseDTO inventario = inventarioService.ajustarStock(dto.getProductoId(), dto.getBodegaId(), dto.getDelta());
        return ResponseEntity.ok(inventario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarInventario(@PathVariable Long id) {
        inventarioService.eliminarInventario(id);
        return ResponseEntity.ok("Inventario eliminado exitosamente");
    }

}
