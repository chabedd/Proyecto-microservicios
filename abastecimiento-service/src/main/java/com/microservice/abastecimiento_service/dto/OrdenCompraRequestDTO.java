package com.microservice.abastecimiento_service.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para generar una nueva orden de compra")
public class OrdenCompraRequestDTO {

    @NotNull(message = "El proveedorId es obligatorio")
    @Schema(description = "ID del proveedor externo al que se le solicita la compra", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long proveedorId;

    @NotEmpty(message = "La OC debe tener al menos un ítem en el detalle")
    @Schema(description = "Lista detallada de ítems a comprar en esta orden", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<@Valid DetalleOrdenCompraRequestDTO> detalles;
}
