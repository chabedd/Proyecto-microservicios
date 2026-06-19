package com.microservice.abastecimiento_service.config;

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
                        .title("Abastecimiento Service API")
                        .version("1.0")
                        .description("API para la creación, seguimiento, recepción y cancelación de órdenes de compra destinadas al reabastecimiento de stock con proveedores externos."));
    }
}
