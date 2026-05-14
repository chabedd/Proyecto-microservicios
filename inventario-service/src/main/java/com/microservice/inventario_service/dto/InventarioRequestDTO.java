package com.microservice.inventario_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioRequestDTO {

    @NotNull(message = "El productoId es obligatorio")
    private Long productoId;

    @NotNull(message = "El bodegaId es obligatorio")
    private Long bodegaId;

    @Min(value = 0, message = "El campo stockActual debe ser mayor o igual a 0")
    private int stockActual;

    @Min(value = 0, message = "El campo stockMinimo debe ser mayor o igual a 0")
    private int stockMinimo;

    @Min(value = 0, message = "El campo stockMaximo debe ser mayor o igual a 0")
    private int stockMaximo;

    @Min(value = 0, message = "El campo puntoReposicion debe ser mayor o igual a 0")
    private int puntoReposicion;
}
