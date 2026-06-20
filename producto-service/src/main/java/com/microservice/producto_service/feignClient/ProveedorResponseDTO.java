package com.microservice.producto_service.feignClient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProveedorResponseDTO {
    private Long id;
    private String nombre;
    private String rut;
    private String email;
    private String telefono;
    private String direccion;
}
