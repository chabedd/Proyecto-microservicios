package com.microservice.producto_service.dto;


import lombok.Data;


@Data
public class ProductoResponseDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private Double precioBase;
    private Long proveedorId;

}
