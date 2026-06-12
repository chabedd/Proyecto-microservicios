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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
@Tag(name = "Proveedores", description = "Endpoints para la gestión del catálogo de proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    @PostMapping
    @Operation(summary = "Crear un nuevo proveedor", description = "Registra un nuevo proveedor en el sistema tras validar que su RUT sea único.")
    @ApiResponse(responseCode = "201", description = "Proveedor creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "409", description = "El RUT ingresado ya pertenece a otro proveedor")
    public ResponseEntity<ProveedorResponseDTO> crearProveedor(@Valid @RequestBody ProveedorRequestDTO proveedorRequest) {
        ProveedorResponseDTO creado = proveedorService.crearProveedor(proveedorRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    @Operation(summary = "Listar todos los proveedores", description = "Retorna la lista de todos los proveedores registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de proveedores obtenida con éxito")
    public ResponseEntity<List<ProveedorResponseDTO>> obtenerTodosProveedores() {
        List<ProveedorResponseDTO> proveedores = proveedorService.obtenerTodosProveedores();
        return ResponseEntity.ok(proveedores);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener proveedor por ID", description = "Retorna los detalles de un proveedor según su identificador único.")
    @ApiResponse(responseCode = "200", description = "Proveedor encontrado y retornado")
    @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    public ResponseEntity<ProveedorResponseDTO> obtenerProveedorPorId(
            @Parameter(description = "ID del proveedor", example = "1", required = true)
            @PathVariable Long id) {
        ProveedorResponseDTO dto = proveedorService.obtenerProveedorPorId(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/rut/{rut}")
    @Operation(summary = "Obtener proveedor por RUT", description = "Busca un proveedor específico por su número de RUT.")
    @ApiResponse(responseCode = "200", description = "Proveedor encontrado y retornado")
    @ApiResponse(responseCode = "404", description = "Proveedor con el RUT especificado no encontrado")
    public ResponseEntity<ProveedorResponseDTO> obtenerProveedorPorRut(
            @Parameter(description = "RUT del proveedor", example = "77.654.321-0", required = true)
            @PathVariable String rut) {
        Optional<ProveedorResponseDTO> proveedor = proveedorService.obtenerProveedorPorRut(rut);
        return proveedor.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar proveedor existente", description = "Actualiza los datos de contacto y comerciales de un proveedor.")
    @ApiResponse(responseCode = "200", description = "Proveedor actualizado con éxito")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    @ApiResponse(responseCode = "409", description = "Conflicto al actualizar (ej. RUT duplicado con otro proveedor)")
    public ResponseEntity<ProveedorResponseDTO> actualizarProveedor(
            @Parameter(description = "ID del proveedor a actualizar", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ProveedorRequestDTO proveedorRequest) {
        ProveedorResponseDTO actualizado = proveedorService.actualizarProveedor(id, proveedorRequest);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un proveedor", description = "Elimina físicamente un proveedor si este no posee órdenes de compra activas en el sistema de abastecimiento.")
    @ApiResponse(responseCode = "204", description = "Proveedor eliminado con éxito")
    @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    @ApiResponse(responseCode = "409", description = "No se puede eliminar el proveedor porque posee órdenes de compra activas")
    public ResponseEntity<Void> eliminarProveedor(
            @Parameter(description = "ID del proveedor a eliminar", example = "1", required = true)
            @PathVariable Long id) {
        proveedorService.eliminarProveedor(id);
        return ResponseEntity.noContent().build();
    }
}
