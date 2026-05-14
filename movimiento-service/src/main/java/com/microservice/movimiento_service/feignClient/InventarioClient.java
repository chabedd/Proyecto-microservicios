package com.microservice.movimiento_service.feignClient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventario-service")
public interface InventarioClient {

    @GetMapping("/api/inventarios/producto/{productoId}")
    List<InventarioResponseDTO> obtenerInventariosPorProducto(@PathVariable Long productoId);

    @GetMapping("/api/inventarios/bodega/{bodegaId}")
    List<InventarioResponseDTO> obtenerInventariosPorBodega(@PathVariable Long bodegaId);

    @PatchMapping("/api/inventarios/ajustar")
    InventarioResponseDTO ajustarStock(@RequestBody AjusteStockDTO dto);
}
