package com.microservice.inventario_service.service;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.inventario_service.dto.InventarioRequestDTO;
import com.microservice.inventario_service.dto.InventarioResponseDTO;
import com.microservice.inventario_service.exception.ManejadorGlobal.InventarioNoEncontradoException;
import com.microservice.inventario_service.exception.ManejadorGlobal.InventarioPersistenciaException;
import com.microservice.inventario_service.feignclient.BodegaClient;
import com.microservice.inventario_service.feignclient.FeignSupport.BodegaDTO;
import com.microservice.inventario_service.feignclient.ProductoClient;
import com.microservice.inventario_service.mapper.InventarioMapper;
import com.microservice.inventario_service.model.Inventario;
import com.microservice.inventario_service.repository.InventarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final InventarioMapper inventarioMapper;
    private final ProductoClient productoClient;
    private final BodegaClient bodegaClient;

    public InventarioResponseDTO crearInventario(InventarioRequestDTO request) {
        Inventario entidad = inventarioMapper.toEntity(request);
        
        // Validar existencia de producto
        productoClient.obtenerProductoPorId(entidad.getProductoId());
        log.info("Producto id={} validado exitosamente", entidad.getProductoId());
        
        // Validar existencia y estado de bodega
        BodegaDTO bodega = bodegaClient.obtenerBodegaPorId(entidad.getBodegaId());
        if (bodega.getActiva() == null || !bodega.getActiva()) {
            throw new IllegalArgumentException("La bodega con id " + entidad.getBodegaId() + " no está activa.");
        }
        log.info("Bodega id={} validada exitosamente (activa)", entidad.getBodegaId());
        
        // Validar que no exista duplicado Producto+Bodega
        if (inventarioRepository.findByProductoIdAndBodegaId(entidad.getProductoId(), entidad.getBodegaId()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un registro de inventario para este producto y bodega.");
        }
        
        validarReglasStock(entidad.getStockActual(), entidad.getStockMinimo(), entidad.getStockMaximo(), entidad.getPuntoReposicion());
        try {
            Inventario saved = inventarioRepository.save(entidad);
            log.info("Inventario creado id={} productoId={} bodegaId={}", saved.getId(), saved.getProductoId(), saved.getBodegaId());
            return inventarioMapper.toResponseDTO(saved);
        } catch (DataAccessException e) {
            log.error("Error de acceso a datos al crear inventario: {}", e.getMessage());
            throw new InventarioPersistenciaException("No pudimos guardar el inventario. Por favor, intenta nuevamente en unos momentos.", e);
        }
    }

    @Transactional(readOnly = true)
    public InventarioResponseDTO obtenerInventarioPorId(Long id) {
        try {
            Inventario inv = obtenerInventarioPorIdInterno(id);
            return inventarioMapper.toResponseDTO(inv);
        } catch (DataAccessException e) {
            log.error("Error al obtener inventario id={}: {}", id, e.getMessage());
            throw new InventarioPersistenciaException("No pudimos cargar el inventario. Por favor, intenta nuevamente.", e);
        }
    }

    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> obtenerTodosInventarios() {
        try {
            List<Inventario> inventarios = inventarioRepository.findAll();
            return inventarioMapper.toResponseDTOList(inventarios);
        } catch (DataAccessException e) {
            log.error("Error al obtener todos los inventarios: {}", e.getMessage());
            throw new InventarioPersistenciaException("No pudimos cargar la lista de inventarios. Por favor, intenta nuevamente.", e);
        }
    }

    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> obtenerInventariosPorProducto(Long productoId) {
        try {
            List<Inventario> inventarios = inventarioRepository.findByProductoId(productoId);
            return inventarioMapper.toResponseDTOList(inventarios);
        } catch (DataAccessException e) {
            log.error("Error al obtener inventarios por producto id={}: {}", productoId, e.getMessage());
            throw new InventarioPersistenciaException("No pudimos cargar los inventarios. Por favor, intenta nuevamente.", e);
        }
    }

    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> obtenerInventariosPorBodega(Long bodegaId) {
        try {
            List<Inventario> inventarios = inventarioRepository.findByBodegaId(bodegaId);
            return inventarioMapper.toResponseDTOList(inventarios);
        } catch (DataAccessException e) {
            log.error("Error al obtener inventarios por bodega id={}: {}", bodegaId, e.getMessage());
            throw new InventarioPersistenciaException("No pudimos cargar los inventarios de la bodega. Por favor, intenta nuevamente.", e);
        }
    }

    public InventarioResponseDTO actualizarStock(Long id, int nuevoStock) {
        Inventario inventario = obtenerInventarioPorIdInterno(id);
        validarReglasStock(nuevoStock, inventario.getStockMinimo(), inventario.getStockMaximo(), inventario.getPuntoReposicion());
        try {
            inventario.setStockActual(nuevoStock);
            Inventario saved = inventarioRepository.save(inventario);
            log.info("Stock actualizado inventarioId={} nuevoStock={}", id, nuevoStock);
            return inventarioMapper.toResponseDTO(saved);
        } catch (DataAccessException e) {
            log.error("Error al actualizar stock id={}: {}", id, e.getMessage());
            throw new InventarioPersistenciaException("No pudimos actualizar el stock. Por favor, intenta nuevamente.", e);
        }
    }

    public void eliminarInventario(Long id) {
        obtenerInventarioPorIdInterno(id);
        try {
            inventarioRepository.deleteById(id);
            log.info("Inventario eliminado id={}", id);
        } catch (DataAccessException e) {
            log.error("Error al eliminar inventario id={}: {}", id, e.getMessage());
            throw new InventarioPersistenciaException("No pudimos eliminar el inventario. Por favor, intenta nuevamente.", e);
        }
    }

    public boolean necesitaReposicion(Long id) {
        Inventario inventario = obtenerInventarioPorIdInterno(id);
        return inventario.getStockActual() <= inventario.getPuntoReposicion();
    }

    public InventarioResponseDTO ajustarStock(Long productoId, Long bodegaId, int delta) {
        Inventario inventario = inventarioRepository.findByProductoIdAndBodegaId(productoId, bodegaId)
                .orElseThrow(() -> new InventarioNoEncontradoException("Inventario no encontrado para productoId: " + productoId + " y bodegaId: " + bodegaId));

        // Validar que la bodega siga activa antes de ajustar
        BodegaDTO bodega = bodegaClient.obtenerBodegaPorId(bodegaId);
        if (bodega.getActiva() == null || !bodega.getActiva()) {
            throw new IllegalArgumentException("La bodega con id " + bodegaId + " no está activa. No se pueden ajustar stocks.");
        }

        int nuevoStock = inventario.getStockActual() + delta;
        validarReglasStock(nuevoStock, inventario.getStockMinimo(), inventario.getStockMaximo(), inventario.getPuntoReposicion());

        try {
            inventario.setStockActual(nuevoStock);
            Inventario saved = inventarioRepository.save(inventario);
            log.info("Ajuste de stock productoId={} bodegaId={} delta={} nuevoStock={}", productoId, bodegaId, delta, saved.getStockActual());
            return inventarioMapper.toResponseDTO(saved);
        } catch (DataAccessException e) {
            log.error("Error al ajustar stock productoId={} bodegaId={}: {}", productoId, bodegaId, e.getMessage());
            throw new InventarioPersistenciaException("No pudimos ajustar el stock. Por favor, intenta nuevamente.", e);
        }
    }

    private void validarReglasStock(int stockActual, int stockMinimo, int stockMaximo, int puntoReposicion) {
        // Validar que el stock no sea negativo
        if (stockActual < 0) {
            throw new IllegalArgumentException("El stock actual no puede ser negativo.");
        }
        
        if (stockMinimo > stockMaximo) {
            throw new IllegalArgumentException("El stock mínimo no puede ser mayor que el stock máximo.");
        }
        
        // Validar que el stock inicial esté dentro de los límites
        if (stockActual < stockMinimo) {
            throw new IllegalArgumentException("El stock actual no puede ser menor que el stock mínimo (" + stockMinimo + ").");
        }
        
        if (stockActual > stockMaximo) {
            throw new IllegalArgumentException("El stock actual no puede superar el stock máximo (" + stockMaximo + ").");
        }
        
        // Validar que el punto de reposición esté estrictamente entre el mínimo y máximo
        if (puntoReposicion <= stockMinimo || puntoReposicion >= stockMaximo) {
            throw new IllegalArgumentException("El punto de reposición debe estar estrictamente entre el stock mínimo (" + stockMinimo + ") y el máximo (" + stockMaximo + ").");
        }
    }
    /**
     * Método privado para obtener entidad por ID (uso interno del servicio)
     */
    private Inventario obtenerInventarioPorIdInterno(Long id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new InventarioNoEncontradoException("Inventario no encontrado con id: " + id));
    }
}
