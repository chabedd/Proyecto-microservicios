package com.microservice.inventario_service.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioResponseDTO {
    private Long id;
    private Long productoId;
    private Long bodegaId;
    private int stockActual;
    private int stockMinimo;
    private int stockMaximo;
    private int puntoReposicion;
    private LocalDateTime ultimaActualizacion;

}
