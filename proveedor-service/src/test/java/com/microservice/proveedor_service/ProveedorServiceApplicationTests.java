package com.microservice.proveedor_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.microservice.proveedor_service.repository.ProveedorRepository;

@SpringBootTest(properties = {
	"spring.cloud.config.enabled=false",
	"spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration"
})
class ProveedorServiceApplicationTests {

	@MockitoBean
	private ProveedorRepository proveedorRepository;

	@Test
	void contextLoads() {
	}

}
