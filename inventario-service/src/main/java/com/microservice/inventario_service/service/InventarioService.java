package com.microservice.inventario_service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.inventario_service.dto.InventarioRequestDTO;
import com.microservice.inventario_service.dto.InventarioResponseDTO;
import com.microservice.inventario_service.exception.ManejadorGlobal.InventarioNoEncontradoException;
import com.microservice.inventario_service.feignclient.BodegaClient;
import com.microservice.inventario_service.feignclient.FeignSupport.BodegaDTO;
import com.microservice.inventario_service.feignclient.ProductoClient;
import com.microservice.inventario_service.mapper.InventarioMapper;
import com.microservice.inventario_service.model.Inventario;
import com.microservice.inventario_service.repository.InventarioRepository;
import com.microservice.inventario_service.validation.InventarioValidator;

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
    private final InventarioValidator inventarioValidator;

    public InventarioResponseDTO crearInventario(InventarioRequestDTO request) {
        Inventario entidad = inventarioMapper.toEntity(request);
        
        // Validar existencia de producto
        productoClient.obtenerProductoPorId(entidad.getProductoId());
        log.info("Producto id={} validado exitosamente", entidad.getProductoId());
        
        // Validar existencia y estado de bodega
        BodegaDTO bodega = bodegaClient.obtenerBodegaPorId(entidad.getBodegaId());
        inventarioValidator.validarBodegaActiva(bodega, entidad.getBodegaId());
        log.info("Bodega id={} validada exitosamente (activa)", entidad.getBodegaId());
        
        // Validar que no exista duplicado Producto+Bodega
        boolean existe = inventarioRepository.findByProductoIdAndBodegaId(entidad.getProductoId(), entidad.getBodegaId()).isPresent();
        inventarioValidator.validarRegistroDuplicado(existe);
        
        inventarioValidator.validarStockNoNegativo(entidad.getStockActual());
        
        Inventario saved = inventarioRepository.save(entidad);
        log.info("Inventario creado id={} productoId={} bodegaId={}", saved.getId(), saved.getProductoId(), saved.getBodegaId());
        return inventarioMapper.toResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public InventarioResponseDTO obtenerInventarioPorId(Long id) {
        Inventario inv = obtenerInventarioPorIdInterno(id);
        return inventarioMapper.toResponseDTO(inv);
    }

    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> obtenerTodosInventarios() {
        List<Inventario> inventarios = inventarioRepository.findAll();
        return inventarioMapper.toResponseDTOList(inventarios);
    }

    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> obtenerInventariosPorProducto(Long productoId) {
        List<Inventario> inventarios = inventarioRepository.findByProductoId(productoId);
        return inventarioMapper.toResponseDTOList(inventarios);
    }

    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> obtenerInventariosPorBodega(Long bodegaId) {
        List<Inventario> inventarios = inventarioRepository.findByBodegaId(bodegaId);
        return inventarioMapper.toResponseDTOList(inventarios);
    }

    public InventarioResponseDTO actualizarStock(Long id, int nuevoStock) {
        Inventario inventario = obtenerInventarioPorIdInterno(id);
        inventarioValidator.validarStockNoNegativo(nuevoStock);
        
        inventario.setStockActual(nuevoStock);
        Inventario saved = inventarioRepository.save(inventario);
        log.info("Stock actualizado inventarioId={} nuevoStock={}", id, nuevoStock);
        return inventarioMapper.toResponseDTO(saved);
    }

    public void eliminarInventario(Long id) {
        obtenerInventarioPorIdInterno(id);
        inventarioRepository.deleteById(id);
        log.info("Inventario eliminado id={}", id);
    }


    public InventarioResponseDTO ajustarStock(Long productoId, Long bodegaId, int delta) {
        // Validar que la bodega siga activa antes de ajustar
        BodegaDTO bodega = bodegaClient.obtenerBodegaPorId(bodegaId);
        inventarioValidator.validarBodegaActiva(bodega, bodegaId);

        Inventario inventario = inventarioRepository.findByProductoIdAndBodegaId(productoId, bodegaId)
                .orElseGet(() -> {
                    // Validar existencia de producto
                    productoClient.obtenerProductoPorId(productoId);

                    Inventario nuevo = new Inventario();
                    nuevo.setProductoId(productoId);
                    nuevo.setBodegaId(bodegaId);
                    nuevo.setStockActual(0);
                    return nuevo;
                });

        int nuevoStock = inventario.getStockActual() + delta;
        inventarioValidator.validarStockNoNegativo(nuevoStock);

        inventario.setStockActual(nuevoStock);
        Inventario saved = inventarioRepository.save(inventario);
        log.info("Ajuste de stock productoId={} bodegaId={} delta={} nuevoStock={}", productoId, bodegaId, delta, saved.getStockActual());
        return inventarioMapper.toResponseDTO(saved);
    }

    /**
     * Método privado para obtener entidad por ID (uso interno del servicio)
     */
    private Inventario obtenerInventarioPorIdInterno(Long id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new InventarioNoEncontradoException("Inventario no encontrado con id: " + id));
    }
}
