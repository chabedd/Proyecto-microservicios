package com.microservice.abastecimiento_service.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.microservice.abastecimiento_service.dto.ProductoResponseDTO;

@FeignClient(name = "producto-service")
public interface ProductoClient {

    @GetMapping("/api/productos/{id}")
    ProductoResponseDTO obtenerProductoPorId(@PathVariable Long id);
}
