package com.microservice.inventario_service.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.microservice.inventario_service.feignclient.FeignSupport.BodegaClientFallback;
import com.microservice.inventario_service.feignclient.FeignSupport.BodegaDTO;

@FeignClient(name = "bodega-service", fallback = BodegaClientFallback.class)
public interface BodegaClient {

    @GetMapping("/api/bodegas/{id}")
    BodegaDTO obtenerBodegaPorId(@PathVariable("id") Long id);
}
