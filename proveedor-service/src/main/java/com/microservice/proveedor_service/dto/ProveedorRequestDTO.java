package com.microservice.proveedor_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El rut es obligatorio")
    private String rut;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato valido")
    private String email;

    @NotBlank(message = "El telefono es obligatorio")
    @Size(min = 7, max = 20, message = "El telefono debe tener entre 7 y 20 caracteres")
    private String telefono;

    @NotBlank(message = "La direccion es obligatoria")
    private String direccion;

}
