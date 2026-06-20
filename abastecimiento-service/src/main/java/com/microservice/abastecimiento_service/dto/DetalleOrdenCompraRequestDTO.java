package com.microservice.abastecimiento_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para el detalle de un ítem en una orden de compra")
public class DetalleOrdenCompraRequestDTO {

    @NotNull(message = "El productoId es obligatorio")
    @Schema(description = "ID del producto a comprar", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productoId;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Schema(description = "Cantidad de unidades a solicitar", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private int cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @Positive(message = "El precio unitario debe ser mayor a 0")
    @Schema(description = "Precio acordado por unidad de producto", example = "1150.0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double precioUnitario;
}
