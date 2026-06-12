package com.microservice.inventario_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos requeridos para realizar un ajuste de stock (incrementar o decrementar)")
public class AjusteStockRequestDto {

    @NotNull(message = "El productoId es obligatorio")
    @Schema(description = "ID del producto a ajustar", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productoId;

    @NotNull(message = "El bodegaId es obligatorio")
    @Schema(description = "ID de la bodega donde se realizará el ajuste", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long bodegaId;

    @NotNull(message = "El delta es obligatorio")
    @Schema(description = "Cantidad de stock a modificar (positivo para sumar, negativo para restar)", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer delta;
}

