package com.microservice.movimiento_service.dto;

import com.microservice.movimiento_service.model.TipoMovimiento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoRequestDTO {

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    private Long bodegaOrigenId;

    private Long bodegaDestinoId;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    private TipoMovimiento tipo;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;
}
