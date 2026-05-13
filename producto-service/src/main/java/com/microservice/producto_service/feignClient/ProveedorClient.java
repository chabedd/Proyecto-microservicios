package com.microservice.producto_service.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; 
@FeignClient(name = "proveedor-service") 
public interface ProveedorClient {
    @GetMapping("/api/proveedores/{id}") 
    Proveedor obtenerProveedorPorId(@PathVariable("id") Long id);
}


