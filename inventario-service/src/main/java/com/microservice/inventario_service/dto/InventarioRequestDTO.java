package com.microservice.inventario_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para inicializar el registro de inventario para un producto en una bodega")
public class InventarioRequestDTO {

    @NotNull(message = "El productoId es obligatorio")
    @Schema(description = "ID del producto", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productoId;

    @NotNull(message = "El bodegaId es obligatorio")
    @Schema(description = "ID de la bodega física", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long bodegaId;

    @Min(value = 0, message = "El campo stockActual debe ser mayor o igual a 0")
    @Schema(description = "Stock inicial del producto en la bodega", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private int stockActual;
}
