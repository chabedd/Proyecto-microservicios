package com.microservice.abastecimiento_service.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventario-service")
public interface InventarioClient {

    @PostMapping("/api/inventarios/ajustar")
    void ajustarStock(@RequestBody AjusteStockDTO dto);
}
