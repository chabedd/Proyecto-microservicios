package com.microservice.movimiento_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.movimiento_service.dto.MovimientoRequestDTO;
import com.microservice.movimiento_service.dto.MovimientoResponseDTO;
import com.microservice.movimiento_service.model.TipoMovimiento;
import com.microservice.movimiento_service.service.MovimientoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
@Tag(name = "Movimientos", description = "Endpoints para el registro y consulta de movimientos de stock (entradas, salidas, transferencias)")
public class MovimientoController {

    private final MovimientoService service;

    @PostMapping
    @Operation(summary = "Registrar un movimiento de stock", description = "Registra una entrada, salida o transferencia de unidades de un producto entre bodegas, actualizando los stocks respectivos.")
    @ApiResponse(responseCode = "201", description = "Movimiento registrado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o insuficientes (ej. stock insuficiente para salida/transferencia)")
    @ApiResponse(responseCode = "404", description = "Producto o bodega de origen/destino no encontrada")
    public ResponseEntity<MovimientoResponseDTO> registrar(@Valid @RequestBody MovimientoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todos los movimientos", description = "Retorna el historial completo de movimientos de stock registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente")
    public ResponseEntity<List<MovimientoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener movimiento por ID", description = "Retorna la información de un movimiento de stock específico mediante su identificador único.")
    @ApiResponse(responseCode = "200", description = "Movimiento encontrado y retornado")
    @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    public ResponseEntity<MovimientoResponseDTO> obtenerPorId(
            @Parameter(description = "ID del movimiento", example = "1", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Buscar movimientos por producto", description = "Historial de movimientos asociados a un producto específico.")
    @ApiResponse(responseCode = "200", description = "Historial del producto obtenido exitosamente")
    public ResponseEntity<List<MovimientoResponseDTO>> obtenerPorProducto(
            @Parameter(description = "ID del producto", example = "1", required = true)
            @PathVariable Long productoId) {
        return ResponseEntity.ok(service.obtenerPorProducto(productoId));
    }

    @GetMapping("/bodega/{bodegaId}")
    @Operation(summary = "Buscar movimientos por bodega", description = "Historial de movimientos asociados a una bodega específica (ya sea como origen o destino).")
    @ApiResponse(responseCode = "200", description = "Historial de la bodega obtenido exitosamente")
    public ResponseEntity<List<MovimientoResponseDTO>> obtenerPorBodega(
            @Parameter(description = "ID de la bodega", example = "1", required = true)
            @PathVariable Long bodegaId) {
        return ResponseEntity.ok(service.obtenerPorBodega(bodegaId));
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Buscar movimientos por tipo", description = "Filtra el historial de movimientos según su tipo: ENTRADA, SALIDA, TRANSFERENCIA.")
    @ApiResponse(responseCode = "200", description = "Movimientos filtrados por tipo obtenidos exitosamente")
    public ResponseEntity<List<MovimientoResponseDTO>> obtenerPorTipo(
            @Parameter(description = "Tipo de movimiento", example = "TRANSFERENCIA", required = true)
            @PathVariable TipoMovimiento tipo) {
        return ResponseEntity.ok(service.obtenerPorTipo(tipo));
    }
}
