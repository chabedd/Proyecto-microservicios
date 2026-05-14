package com.microservice.movimiento_service.dto;

import java.time.LocalDateTime;

import com.microservice.movimiento_service.model.TipoMovimiento;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoResponseDTO {

    private Long id;
    private Long productoId;
    private Long bodegaOrigenId;
    private Long bodegaDestinoId;
    private TipoMovimiento tipo;
    private Integer cantidad;
    private String motivo;
    private LocalDateTime fecha;
}
