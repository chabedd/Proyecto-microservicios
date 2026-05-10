package com.microservices.config_microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
public class ConfigMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigMicroserviceApplication.class, args);
	}

}
