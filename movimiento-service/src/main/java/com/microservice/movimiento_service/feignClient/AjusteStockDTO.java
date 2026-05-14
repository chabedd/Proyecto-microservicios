package com.microservice.movimiento_service.feignClient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AjusteStockDTO {

    private Long productoId;
    private Long bodegaId;
    private int delta;
}
