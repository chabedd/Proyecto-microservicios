package com.microservice.abastecimiento_service.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrdenCompraRequestDTO {
    @NotNull 
    private Long productoId;
    @NotNull 
    private int cantidad;

}
