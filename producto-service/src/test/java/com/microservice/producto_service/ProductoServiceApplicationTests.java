package com.microservice.producto_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.microservice.producto_service.feignClient.InventarioClient;
import com.microservice.producto_service.repository.ProductoRepository;

@SpringBootTest(properties = {
	"spring.cloud.config.enabled=false",
	"spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration"
})
class ProductoServiceApplicationTests {

	@MockitoBean
	private ProductoRepository productoRepository;

	@MockitoBean
	private InventarioClient inventarioClient;

	@Test
	void contextLoads() {
	}

}
