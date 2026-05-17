package com.microservice.abastecimiento_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleOrdenCompraRequestDTO {

    @NotNull(message = "El productoId es obligatorio")
    private Long productoId;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private int cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @Positive(message = "El precio unitario debe ser mayor a 0")
    private Double precioUnitario;
}
