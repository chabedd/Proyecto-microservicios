package com.microservice.inventario_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.microservice.inventario_service.feignclient.BodegaClient;
import com.microservice.inventario_service.feignclient.ProductoClient;
import com.microservice.inventario_service.repository.InventarioRepository;

@SpringBootTest(properties = {
	"spring.cloud.config.enabled=false",
	"spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration"
})
class InventarioServiceApplicationTests {

	@MockitoBean
	private InventarioRepository inventarioRepository;

	@MockitoBean
	private ProductoClient productoClient;

	@MockitoBean
	private BodegaClient bodegaClient;

	@Test
	void contextLoads() {
	}

}
