package com.microservice.producto_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoRequestDTO {

    @NotBlank(message = "El código SKU es obligatorio")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El precio base es obligatorio")
    @Positive(message = "El precio base debe ser mayor a 0")
    private Double precioBase;

    @NotNull(message = "El proveedorId es obligatorio")
    private Long proveedorId;
}
