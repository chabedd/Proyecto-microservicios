package com.microservice.movimiento_service.dto;

import java.time.LocalDateTime;

import com.microservice.movimiento_service.model.TipoMovimiento;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Detalles del movimiento de stock registrado en el sistema")
public class MovimientoResponseDTO {

    @Schema(description = "ID único del movimiento", example = "1")
    private Long id;

    @Schema(description = "ID del producto involucrado", example = "1")
    private Long productoId;

    @Schema(description = "ID de la bodega de origen (nulo para ENTRADA)", example = "1")
    private Long bodegaOrigenId;

    @Schema(description = "ID de la bodega de destino (nulo para SALIDA)", example = "2")
    private Long bodegaDestinoId;

    @Schema(description = "Tipo de movimiento realizado: ENTRADA, SALIDA o TRANSFERENCIA", example = "TRANSFERENCIA")
    private TipoMovimiento tipo;

    @Schema(description = "Cantidad de unidades transferidas/ajustadas", example = "50")
    private Integer cantidad;

    @Schema(description = "Razón o motivo del movimiento", example = "Reubicación de stock estacional")
    private String motivo;

    @Schema(description = "Fecha y hora en que se registró el movimiento", example = "2026-06-07T00:00:00")
    private LocalDateTime fecha;
}
