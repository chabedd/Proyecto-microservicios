package com.microservice.bodega_service.controller;

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

import com.microservice.bodega_service.dto.BodegaRequest;
import com.microservice.bodega_service.dto.BodegaResponse;
import com.microservice.bodega_service.service.BodegaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bodegas")
@RequiredArgsConstructor
@Tag(name = "Bodegas", description = "Endpoints para la gestión de bodegas físicas")
public class BodegaController {

    private final BodegaService service;

    @GetMapping
    @Operation(summary = "Listar todas las bodegas", description = "Retorna una lista completa de todas las bodegas físicas registradas en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de bodegas obtenida con éxito")
    public ResponseEntity<List<BodegaResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar bodega por ID", description = "Retorna la información detallada de una bodega según su ID único.")
    @ApiResponse(responseCode = "200", description = "Bodega encontrada y retornada")
    @ApiResponse(responseCode = "404", description = "Bodega no encontrada")
    public ResponseEntity<BodegaResponse> buscarPorId(
            @Parameter(description = "ID único de la bodega", example = "1", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar una nueva bodega", description = "Registra una nueva bodega física en el sistema configurando su nombre, ubicación y capacidad máxima.")
    @ApiResponse(responseCode = "201", description = "Bodega creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "El nombre de la bodega ya está registrado")
    public ResponseEntity<BodegaResponse> guardar(@Valid @RequestBody BodegaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una bodega", description = "Elimina físicamente una bodega del sistema si cumple con las restricciones (por ejemplo, que no contenga productos con stock activo).")
    @ApiResponse(responseCode = "204", description = "Bodega eliminada exitosamente")
    @ApiResponse(responseCode = "404", description = "Bodega no encontrada")
    @ApiResponse(responseCode = "409", description = "No se puede eliminar la bodega porque tiene stock o transacciones activas asociadas")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la bodega a eliminar", example = "1", required = true)
            @PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
