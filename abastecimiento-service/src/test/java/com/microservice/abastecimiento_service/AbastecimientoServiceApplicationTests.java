package com.microservice.abastecimiento_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.microservice.abastecimiento_service.feignclient.InventarioClient;
import com.microservice.abastecimiento_service.feignclient.ProductoClient;
import com.microservice.abastecimiento_service.feignclient.ProveedorClient;
import com.microservice.abastecimiento_service.repository.OrdenCompraRepository;

@SpringBootTest(properties = {
	"spring.cloud.config.enabled=false",
	"spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration"
})
class AbastecimientoServiceApplicationTests {

	@MockitoBean
	private OrdenCompraRepository ordenCompraRepository;

	@MockitoBean
	private InventarioClient inventarioClient;

	@MockitoBean
	private ProductoClient productoClient;

	@MockitoBean
	private ProveedorClient proveedorClient;

	@Test
	void contextLoads() {
	}

}
