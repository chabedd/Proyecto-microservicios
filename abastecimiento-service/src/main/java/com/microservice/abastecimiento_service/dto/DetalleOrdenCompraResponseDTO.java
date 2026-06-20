package com.microservice.abastecimiento_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detalle de un ítem devuelto en la orden de compra")
public class DetalleOrdenCompraResponseDTO {

    @Schema(description = "ID único del detalle de orden de compra", example = "1")
    private Long id;

    @Schema(description = "ID del producto comprado", example = "1")
    private Long productoId;

    @Schema(description = "Cantidad solicitada", example = "100")
    private int cantidad;

    @Schema(description = "Precio unitario acordado", example = "1150.0")
    private Double precioUnitario;
}
