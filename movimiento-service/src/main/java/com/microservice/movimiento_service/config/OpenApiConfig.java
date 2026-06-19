package com.microservice.movimiento_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Movimiento Service API")
                        .version("1.0")
                        .description("API para registrar y auditar todos los movimientos de inventario (entradas, salidas y traslados) entre bodegas."));
    }
}
