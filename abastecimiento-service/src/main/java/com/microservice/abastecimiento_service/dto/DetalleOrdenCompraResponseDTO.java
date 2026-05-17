package com.microservice.abastecimiento_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleOrdenCompraResponseDTO {
    private Long id;
    private Long productoId;
    private int cantidad;
    private Double precioUnitario;
}
