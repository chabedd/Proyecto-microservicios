package com.microservice.proveedor_service.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detalles del proveedor registrado en el sistema")
public class ProveedorResponseDTO {

    @Schema(description = "ID único autogenerado del proveedor", example = "1")
    private Long id;

    @Schema(description = "Nombre de la empresa o proveedor", example = "Distribuidora Industrial S.A.")
    private String nombre;

    @Schema(description = "RUT único del proveedor", example = "77.654.321-0")
    private String rut;

    @Schema(description = "Email de contacto del proveedor", example = "contacto@distribuidorasa.cl")
    private String email;

    @Schema(description = "Teléfono de contacto del proveedor", example = "+56912345678")
    private String telefono;

    @Schema(description = "Dirección comercial del proveedor", example = "Av. Providencia 1234, Oficina 501, Santiago")
    private String direccion;

    @Schema(description = "Fecha y hora de registro del proveedor", example = "2026-06-07T00:00:00")
    private LocalDateTime fechaRegistro;
}
