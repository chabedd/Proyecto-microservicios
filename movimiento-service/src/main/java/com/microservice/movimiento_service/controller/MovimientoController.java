package com.microservice.movimiento_service.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.microservice.movimiento_service.dto.MovimientoRequestDTO;
import com.microservice.movimiento_service.dto.MovimientoResponseDTO;
import com.microservice.movimiento_service.model.TipoMovimiento;
import com.microservice.movimiento_service.service.MovimientoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService service;

    @PostMapping
    public ResponseEntity<MovimientoResponseDTO> registrar(@Valid @RequestBody MovimientoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<MovimientoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<MovimientoResponseDTO>> obtenerPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(service.obtenerPorProducto(productoId));
    }

    @GetMapping("/bodega/{bodegaId}")
    public ResponseEntity<List<MovimientoResponseDTO>> obtenerPorBodega(@PathVariable Long bodegaId) {
        return ResponseEntity.ok(service.obtenerPorBodega(bodegaId));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<MovimientoResponseDTO>> obtenerPorTipo(@PathVariable TipoMovimiento tipo) {
        return ResponseEntity.ok(service.obtenerPorTipo(tipo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimientoResponseDTO> actualizar(
            @PathVariable Long id, @Valid @RequestBody MovimientoRequestDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
