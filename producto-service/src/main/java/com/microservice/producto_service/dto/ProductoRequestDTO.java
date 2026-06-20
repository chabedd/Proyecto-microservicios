package com.microservice.producto_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos requeridos para la creación o actualización de un producto")
public class ProductoRequestDTO {

    @NotBlank(message = "El código SKU es obligatorio")
    @Schema(description = "Código SKU único del producto", example = "PROD-100", requiredMode = Schema.RequiredMode.REQUIRED)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre descriptivo del producto", example = "Tornillo de Acero 1/2", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @NotNull(message = "El precio base es obligatorio")
    @Positive(message = "El precio base debe ser mayor a 0")
    @Schema(description = "Precio unitario base del producto", example = "1250.0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double precioBase;

    @NotNull(message = "El proveedorId es obligatorio")
    @Schema(description = "ID del proveedor que provee el producto", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long proveedorId;
}
