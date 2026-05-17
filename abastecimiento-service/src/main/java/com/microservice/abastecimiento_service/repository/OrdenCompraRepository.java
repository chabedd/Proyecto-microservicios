package com.microservice.abastecimiento_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microservice.abastecimiento_service.model.OrdenCompra;
import com.microservice.abastecimiento_service.model.TipoEstado;

public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {

    List<OrdenCompra> findByProveedorId(Long proveedorId);

    boolean existsByProveedorIdAndEstadoIn(Long proveedorId, List<TipoEstado> estados);
}
