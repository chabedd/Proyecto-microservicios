package com.microservice.abastecimiento_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservice.abastecimiento_service.model.OrdenCompra;

public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {
    

}
