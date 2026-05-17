package com.microservice.producto_service.feignClient;

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
}
