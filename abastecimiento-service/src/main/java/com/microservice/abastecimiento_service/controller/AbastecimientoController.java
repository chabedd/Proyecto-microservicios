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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/abastecimiento")
@RequiredArgsConstructor
@Tag(name = "Abastecimiento", description = "Endpoints para la gestión y control de órdenes de compra con proveedores externos")
public class AbastecimientoController {

    private final AbastecimientoService service;

    @PostMapping
    @Operation(summary = "Crear orden de compra", description = "Registra una nueva orden de compra en estado SOLICITADA tras verificar la existencia del proveedor y validar los productos solicitados.")
    @ApiResponse(responseCode = "201", description = "Orden de compra creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de la solicitud inválidos o vacíos")
    @ApiResponse(responseCode = "404", description = "El proveedor especificado no existe o alguno de los productos no fue encontrado")
    public ResponseEntity<OrdenCompraResponseDTO> crear(@Valid @RequestBody OrdenCompraRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearOrden(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todas las órdenes de compra", description = "Retorna el listado completo de todas las órdenes de compra registradas en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de órdenes de compra obtenida con éxito")
    public ResponseEntity<List<OrdenCompraResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener orden de compra por ID", description = "Busca y retorna la información detallada de una orden de compra mediante su ID.")
    @ApiResponse(responseCode = "200", description = "Orden de compra encontrada y retornada")
    @ApiResponse(responseCode = "404", description = "Orden de compra no encontrada")
    public ResponseEntity<OrdenCompraResponseDTO> obtener(
            @Parameter(description = "ID de la orden de compra", example = "1", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PutMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de una orden", description = "Permite transicionar el estado de la orden de compra (por ejemplo, a RECIBIDA). Si pasa a RECIBIDA, se requiere indicar bodegaId para ingresar automáticamente el stock al inventario.")
    @ApiResponse(responseCode = "200", description = "Estado cambiado exitosamente")
    @ApiResponse(responseCode = "400", description = "Transición de estado no válida o falta el bodegaId al marcar como RECIBIDA")
    @ApiResponse(responseCode = "404", description = "Orden de compra o bodega no encontrada")
    public ResponseEntity<OrdenCompraResponseDTO> cambiarEstado(
            @Parameter(description = "ID de la orden de compra", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado a aplicar: SOLICITADA, RECIBIDA, CANCELADA", example = "RECIBIDA", required = true)
            @RequestParam String estado,
            @Parameter(description = "ID de la bodega física donde ingresará el stock (obligatorio solo si el estado es RECIBIDA)", example = "2")
            @RequestParam(required = false) Long bodegaId) {
        return ResponseEntity.ok(service.cambiarEstado(id, estado, bodegaId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar una orden de compra", description = "Cancela una orden de compra (marcando su estado como CANCELADA) si el estado actual lo permite.")
    @ApiResponse(responseCode = "204", description = "Orden de compra cancelada con éxito")
    @ApiResponse(responseCode = "400", description = "No se puede cancelar la orden en su estado actual (ej. ya estaba recibida o cancelada)")
    @ApiResponse(responseCode = "404", description = "Orden de compra no encontrada")
    public ResponseEntity<Void> cancelarOrden(
            @Parameter(description = "ID de la orden de compra a cancelar", example = "1", required = true)
            @PathVariable Long id) {
        service.cancelarOrden(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/proveedor/{proveedorId}/tiene-ordenes-activas")
    @Operation(summary = "Verificar órdenes activas de un proveedor", description = "Consulta interna para comprobar si el proveedor especificado tiene órdenes activas en el sistema. Utilizado para validación antes de eliminación.")
    @ApiResponse(responseCode = "200", description = "Consulta exitosa, retorna boolean indicando si tiene órdenes activas")
    public ResponseEntity<Boolean> tieneOrdenesActivas(
            @Parameter(description = "ID del proveedor", example = "1", required = true)
            @PathVariable Long proveedorId) {
        return ResponseEntity.ok(service.tieneOrdenesActivasPorProveedor(proveedorId));
    }
}
