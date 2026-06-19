package com.microservice.producto_service.config;

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
                        .title("Producto Service API")
                        .version("1.0")
                        .description("API para la gestión y administración de productos (catálogo, SKU, precios base y asociaciones con proveedores) en el sistema de microservicios."));
    }
}
