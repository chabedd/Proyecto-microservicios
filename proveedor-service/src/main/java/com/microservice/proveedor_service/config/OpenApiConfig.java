package com.microservice.proveedor_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Proveedor Service API")
                        .version("1.0")
                        .description("API para la gestión del catálogo de proveedores externos, validaciones de RUT y información de contacto."));
    }
}
