package com.microservice.bodega_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Información detallada de la bodega registrada")
public class BodegaResponse {

    @Schema(description = "ID único autogenerado de la bodega", example = "1")
    private Long id;

    @Schema(description = "Nombre identificativo de la bodega", example = "Bodega Central Norte")
    private String nombre;

    @Schema(description = "Ubicación geográfica de la bodega", example = "Avenida Industrial 450, Quilicura")
    private String ubicacion;

    @Schema(description = "Capacidad máxima total de ítems", example = "5000")
    private Integer capacidadMaximaItems;
}
