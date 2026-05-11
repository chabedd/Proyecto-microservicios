package com.microservice.inventario_service.service;

import com.microservice.inventario_service.model.Inventario;
import com.microservice.inventario_service.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InventarioService {

    private final InventarioRepository inventarioRepository;

    public Inventario crearInventario(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }

    @Transactional(readOnly = true)
    public Inventario obtenerInventarioPorId(Long id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Inventario> obtenerTodosInventarios() {
        return inventarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Inventario> obtenerInventariosPorProducto(Long productoId) {
        return inventarioRepository.findByProductoId(productoId);
    }

    @Transactional(readOnly = true)
    public List<Inventario> obtenerInventariosPorBodega(Long bodegaId) {
        return inventarioRepository.findByBodegaId(bodegaId);
    }

    public Inventario actualizarStock(Long id, int nuevoStock) {
        Inventario inventario = obtenerInventarioPorId(id);
        inventario.setStockActual(nuevoStock);
        return inventarioRepository.save(inventario);
    }

    public void eliminarInventario(Long id) {
        if (!inventarioRepository.existsById(id)) {
            throw new RuntimeException("Inventario no encontrado con id: " + id);
        }
        inventarioRepository.deleteById(id);
    }

    public boolean necesitaReposicion(Long id) {
        Inventario inventario = obtenerInventarioPorId(id);
        return inventario.getStockActual() <= inventario.getPuntoReposicion();
    }

}
