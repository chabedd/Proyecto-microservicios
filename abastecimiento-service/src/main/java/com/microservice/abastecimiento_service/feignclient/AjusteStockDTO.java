package com.microservice.abastecimiento_service.feignclient;

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
