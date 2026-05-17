package com.microservice.abastecimiento_service.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompraRequestDTO {

    @NotNull(message = "El proveedorId es obligatorio")
    private Long proveedorId;

    @NotEmpty(message = "La OC debe tener al menos un ítem en el detalle")
    private List<@Valid DetalleOrdenCompraRequestDTO> detalles;
}
