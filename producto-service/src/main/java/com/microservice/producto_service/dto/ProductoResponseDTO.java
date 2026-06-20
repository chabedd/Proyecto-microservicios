package com.microservice.producto_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información detallada de la respuesta de un producto")
public class ProductoResponseDTO {

    @Schema(description = "ID único autogenerado del producto", example = "1")
    private Long id;

    @Schema(description = "Código SKU único del producto", example = "PROD-100")
    private String codigo;

    @Schema(description = "Nombre descriptivo del producto", example = "Tornillo de Acero 1/2")
    private String nombre;

    @Schema(description = "Precio unitario base del producto", example = "1250.0")
    private Double precioBase;

    @Schema(description = "ID del proveedor asociado", example = "1")
    private Long proveedorId;

    @Schema(description = "Indica si el producto está activo", example = "true")
    private Boolean activo;
}
