package com.microservice.inventario_service.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.microservice.inventario_service.feignclient.FeignSupport.ProductoClientFallback;
import com.microservice.inventario_service.feignclient.FeignSupport.ProductoDTO;

@FeignClient(name = "producto-service", fallback = ProductoClientFallback.class)
public interface ProductoClient {

    @GetMapping("/api/productos/{id}")
    ProductoDTO obtenerProductoPorId(@PathVariable("id") Long id);
}
