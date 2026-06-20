package com.microservice.abastecimiento_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representación simplificada de la respuesta de un producto en el módulo de abastecimiento")
public class ProductoResponseDTO {
    
    @Schema(description = "ID único del producto", example = "1")
    private Long id;
    
    @Schema(description = "ID del proveedor asociado al producto", example = "1")
    private Long proveedorId;

    @Schema(description= "Estado de disponibilidad del producto", example= "activo")
    private Boolean activo;
}
