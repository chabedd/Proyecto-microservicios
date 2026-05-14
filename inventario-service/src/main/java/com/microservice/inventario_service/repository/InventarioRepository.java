package com.microservice.inventario_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microservice.inventario_service.model.Inventario;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    List<Inventario> findByProductoId(Long productoId);
    List<Inventario> findByBodegaId(Long bodegaId);
    Optional<Inventario> findByProductoIdAndBodegaId(Long productoId, Long bodegaId);
}
