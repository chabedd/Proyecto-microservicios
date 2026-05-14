
package com.microservice.inventario_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AjusteStockRequestDTO {

    private Long productoId;
    private Long bodegaId;
    private int delta;
}

