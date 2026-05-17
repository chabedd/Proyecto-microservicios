package com.microservice.abastecimiento_service.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "proveedor-service")
public interface ProveedorClient {

    @GetMapping("/api/proveedores/{id}")
    ProveedorResponseDTO obtenerProveedorPorId(@PathVariable Long id);
}
