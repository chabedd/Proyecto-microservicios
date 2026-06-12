package com.microservice.inventario_service.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detalles del stock e inventario asociado a un producto y bodega")
public class InventarioResponseDTO {

    @Schema(description = "ID único del registro de inventario", example = "1")
    private Long id;

    @Schema(description = "ID del producto asociado", example = "1")
    private Long productoId;

    @Schema(description = "ID de la bodega física asociada", example = "2")
    private Long bodegaId;

    @Schema(description = "Cantidad de stock disponible actual", example = "150")
    private int stockActual;

    @Schema(description = "Fecha y hora de la última modificación del stock", example = "2026-06-07T00:00:00")
    private LocalDateTime ultimaActualizacion;
}
