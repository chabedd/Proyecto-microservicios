package com.microservice.movimiento_service.feignClient;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
