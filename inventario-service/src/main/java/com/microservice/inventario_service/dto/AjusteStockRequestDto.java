package com.microservice.inventario_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AjusteStockRequestDTO {

    @NotNull(message = "El productoId es obligatorio")
    private Long productoId;

    @NotNull(message = "El bodegaId es obligatorio")
    private Long bodegaId;

    @NotNull(message = "El delta es obligatorio")
    private Integer delta;
}

