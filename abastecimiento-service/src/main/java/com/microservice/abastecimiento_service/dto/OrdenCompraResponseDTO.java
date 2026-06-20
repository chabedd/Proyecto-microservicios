package com.microservice.abastecimiento_service.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.microservice.abastecimiento_service.model.TipoEstado;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detalles de la orden de compra devuelta por el sistema")
public class OrdenCompraResponseDTO {

    @Schema(description = "ID único autogenerado de la orden de compra", example = "1")
    private Long id;

    @Schema(description = "ID del proveedor asociado", example = "1")
    private Long proveedorId;

    @Schema(description = "Estado actual de la orden: SOLICITADA, RECIBIDA, CANCELADA", example = "SOLICITADA")
    private TipoEstado estado;

    @Schema(description = "Fecha y hora de creación de la orden de compra", example = "2026-06-07T00:00:00")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Lista de ítems incluidos en la orden de compra")
    private List<DetalleOrdenCompraResponseDTO> detalles;
}
