package com.microservice.movimiento_service.dto;

import com.microservice.movimiento_service.model.TipoMovimiento;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos para registrar un nuevo movimiento de inventario")
public class MovimientoRequestDTO {

    @NotNull(message = "El producto es obligatorio")
    @Schema(description = "ID del producto", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productoId;

    @Schema(description = "ID de la bodega de origen (requerido para SALIDA o TRANSFERENCIA)", example = "1")
    private Long bodegaOrigenId;

    @Schema(description = "ID de la bodega de destino (requerido para ENTRADA o TRANSFERENCIA)", example = "2")
    private Long bodegaDestinoId;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    @Schema(description = "Tipo de movimiento de stock: ENTRADA, SALIDA, TRANSFERENCIA", example = "TRANSFERENCIA", requiredMode = Schema.RequiredMode.REQUIRED)
    private TipoMovimiento tipo;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    @Schema(description = "Cantidad de unidades del movimiento", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer cantidad;

    @NotBlank(message = "El motivo es obligatorio")
    @Schema(description = "Motivo detallado de la transacción", example = "Reubicación de stock estacional", requiredMode = Schema.RequiredMode.REQUIRED)
    private String motivo;
}
