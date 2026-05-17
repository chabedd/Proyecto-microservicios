package com.microservice.abastecimiento_service.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.microservice.abastecimiento_service.model.TipoEstado;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompraResponseDTO {
    private Long id;
    private Long proveedorId;
    private TipoEstado estado;
    private LocalDateTime fechaCreacion;
    private List<DetalleOrdenCompraResponseDTO> detalles;
}
