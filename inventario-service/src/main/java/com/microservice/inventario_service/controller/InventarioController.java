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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventarios")
@RequiredArgsConstructor
@Tag(name = "Inventarios", description = "Endpoints para el control y ajuste del inventario de productos en bodegas")
public class InventarioController {

    private final InventarioService inventarioService;

    @PostMapping
    @Operation(summary = "Crear registro de inventario", description = "Inicializa el registro de stock para un producto en una bodega específica.")
    @ApiResponse(responseCode = "201", description = "Registro de inventario creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "El producto ya se encuentra registrado en esa bodega")
    public ResponseEntity<InventarioResponseDTO> crearInventario(@Valid @RequestBody InventarioRequestDTO requestDTO) {
        InventarioResponseDTO responseDTO = inventarioService.crearInventario(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    @Operation(summary = "Listar todos los registros de inventario", description = "Retorna una lista completa con los niveles de stock de todos los productos en todas las bodegas.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida con éxito")
    public ResponseEntity<List<InventarioResponseDTO>> obtenerTodosInventarios() {
        List<InventarioResponseDTO> inventarios = inventarioService.obtenerTodosInventarios();
        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener inventario por ID", description = "Retorna la información detallada de stock de un registro específico de inventario.")
    @ApiResponse(responseCode = "200", description = "Registro de inventario retornado exitosamente")
    @ApiResponse(responseCode = "404", description = "Registro de inventario no encontrado")
    public ResponseEntity<InventarioResponseDTO> obtenerInventarioPorId(
            @Parameter(description = "ID del registro de inventario", example = "1", required = true)
            @PathVariable Long id) {
        InventarioResponseDTO inventario = inventarioService.obtenerInventarioPorId(id);
        return ResponseEntity.ok(inventario);
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Buscar inventario por ID de Producto", description = "Obtiene los registros de stock en todas las bodegas para un producto específico.")
    @ApiResponse(responseCode = "200", description = "Registros obtenidos exitosamente")
    public ResponseEntity<List<InventarioResponseDTO>> obtenerInventariosPorProducto(
            @Parameter(description = "ID del producto a consultar", example = "1", required = true)
            @PathVariable Long productoId) {
        List<InventarioResponseDTO> inventarios = inventarioService.obtenerInventariosPorProducto(productoId);
        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/bodega/{bodegaId}")
    @Operation(summary = "Buscar inventario por ID de Bodega", description = "Retorna el stock de todos los productos que se encuentran en una bodega específica.")
    @ApiResponse(responseCode = "200", description = "Registros de la bodega obtenidos exitosamente")
    public ResponseEntity<List<InventarioResponseDTO>> obtenerInventariosPorBodega(
            @Parameter(description = "ID de la bodega a consultar", example = "1", required = true)
            @PathVariable Long bodegaId) {
        List<InventarioResponseDTO> inventarios = inventarioService.obtenerInventariosPorBodega(bodegaId);
        return ResponseEntity.ok(inventarios);
    }

    @PostMapping("/ajustar")
    @Operation(summary = "Ajustar stock de un producto", description = "Incrementa o decrementa el stock de un producto específico en una bodega específica, registrando el movimiento correspondiente.")
    @ApiResponse(responseCode = "200", description = "Stock ajustado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o insuficientes (ej. stock resultante negativo)")
    @ApiResponse(responseCode = "404", description = "Registro de inventario no encontrado para la combinación de producto y bodega")
    public ResponseEntity<InventarioResponseDTO> ajustarStockPost(@Valid @RequestBody AjusteStockRequestDto dto) {
        InventarioResponseDTO inventario = inventarioService.ajustarStock(dto.getProductoId(), dto.getBodegaId(), dto.getDelta());
        return ResponseEntity.ok(inventario);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar registro de inventario", description = "Elimina físicamente el registro de stock del sistema.")
    @ApiResponse(responseCode = "200", description = "Registro de inventario eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Registro de inventario no encontrado")
    public ResponseEntity<String> eliminarInventario(
            @Parameter(description = "ID del registro de inventario a eliminar", example = "1", required = true)
            @PathVariable Long id) {
        inventarioService.eliminarInventario(id);
        return ResponseEntity.ok("Inventario eliminado exitosamente");
    }
}
