package com.microservice.movimiento_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservice.movimiento_service.model.Movimiento;
import com.microservice.movimiento_service.model.TipoMovimiento;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByProductoId(Long productoId);

    List<Movimiento> findByBodegaOrigenId(Long bodegaOrigenId);

    List<Movimiento> findByTipo(TipoMovimiento tipo);
}
