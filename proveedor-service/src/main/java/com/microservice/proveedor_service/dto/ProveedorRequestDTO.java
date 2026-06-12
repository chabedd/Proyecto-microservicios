package com.microservice.proveedor_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos necesarios para registrar o actualizar un proveedor")
public class ProveedorRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre de la empresa o proveedor", example = "Distribuidora Industrial S.A.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @NotBlank(message = "El rut es obligatorio")
    @Schema(description = "RUT único del proveedor", example = "77.654.321-0", requiredMode = Schema.RequiredMode.REQUIRED)
    private String rut;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato valido")
    @Schema(description = "Email de contacto del proveedor", example = "contacto@distribuidorasa.cl", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "El telefono es obligatorio")
    @Size(min = 7, max = 20, message = "El telefono debe tener entre 7 y 20 caracteres")
    @Schema(description = "Teléfono de contacto del proveedor", example = "+56912345678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String telefono;

    @NotBlank(message = "La direccion es obligatoria")
    @Schema(description = "Dirección comercial del proveedor", example = "Av. Providencia 1234, Oficina 501, Santiago", requiredMode = Schema.RequiredMode.REQUIRED)
    private String direccion;
}
