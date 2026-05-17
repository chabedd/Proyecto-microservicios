package com.microservice.producto_service.feignClient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventario-service")
public interface InventarioClient {

    @GetMapping("/api/inventarios/producto/{productoId}")
    List<InventarioResponseDTO> obtenerInventariosPorProducto(@PathVariable Long productoId);
}
