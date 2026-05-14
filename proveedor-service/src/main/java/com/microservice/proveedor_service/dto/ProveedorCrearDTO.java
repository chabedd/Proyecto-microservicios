package com.microservice.proveedor_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorCrearDTO {

    private String nombre;
    private String rut;
    private String email;
    private String telefono;
    private String direccion;

}
