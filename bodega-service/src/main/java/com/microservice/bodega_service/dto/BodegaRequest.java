package com.microservice.bodega_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Datos necesarios para registrar o actualizar una bodega física")
public class BodegaRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre identificativo de la bodega", example = "Bodega Central Norte", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @NotBlank(message = "La ubicación es obligatoria")
    @Schema(description = "Dirección o ubicación geográfica de la bodega", example = "Avenida Industrial 450, Quilicura", requiredMode = Schema.RequiredMode.REQUIRED)
    private String ubicacion;

    @NotNull(message = "La capacidad es obligatoria")
    @Positive(message = "La capacidad debe ser mayor a 0")
    @Schema(description = "Capacidad máxima total de ítems que puede almacenar la bodega", example = "5000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer capacidadMaximaItems;
}


