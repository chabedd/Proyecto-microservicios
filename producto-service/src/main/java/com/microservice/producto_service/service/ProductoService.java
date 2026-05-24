package com.microservice.producto_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.producto_service.dto.ProductoRequestDTO;
import com.microservice.producto_service.dto.ProductoResponseDTO;
import com.microservice.producto_service.exception.ManejadorGlobal.ProductoConStockException;
import com.microservice.producto_service.exception.ManejadorGlobal.ProductoNoEncontradoException;
import com.microservice.producto_service.exception.ManejadorGlobal.ProductoValidacionException;
import com.microservice.producto_service.exception.ManejadorGlobal.SkuDuplicadoException;
import com.microservice.producto_service.feignClient.InventarioClient;
import com.microservice.producto_service.model.Producto;
import com.microservice.producto_service.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor

public class ProductoService {

    private final ProductoRepository repository;
    private final InventarioClient inventarioClient;

    @Transactional
    public ProductoResponseDTO crear(ProductoRequestDTO dto) {
        validarPrecioBase(dto.getPrecioBase());
        if (repository.existsByCodigo(dto.getCodigo())) {
            throw new SkuDuplicadoException("Ya existe un producto con el SKU: " + dto.getCodigo());
        }
        Producto p = new Producto();
        p.setCodigo(dto.getCodigo());
        p.setNombre(dto.getNombre());
        p.setPrecioBase(dto.getPrecioBase());
        p.setProveedorId(dto.getProveedorId());
        try {
            Producto saved = repository.save(p);
            
            return mapearADTO(saved);
        } catch (DataIntegrityViolationException e) {
            throw new SkuDuplicadoException("Ya existe un producto con el SKU: " + dto.getCodigo());
        }
    }

    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerTodos() {
        List<Producto> listaP = repository.findAll();
        List <ProductoResponseDTO> productos = new ArrayList<>();
        for (Producto p : listaP) {
            productos.add(mapearADTO(p));
            
        }
        return productos;
    }

    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerPorId(Long id) {
        return mapearADTO(obtenerEntidad(id));
    }

    @Transactional
    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO dto) {
        validarPrecioBase(dto.getPrecioBase());
        Producto p = obtenerEntidad(id);
        if (!p.getCodigo().equals(dto.getCodigo()) && repository.existsByCodigo(dto.getCodigo())) {
            throw new SkuDuplicadoException("Ya existe un producto con el SKU: " + dto.getCodigo());
        }
        p.setCodigo(dto.getCodigo());
        p.setNombre(dto.getNombre());
        p.setPrecioBase(dto.getPrecioBase());
        p.setProveedorId(dto.getProveedorId());
        try {
            Producto saved = repository.save(p);
            return mapearADTO(saved);
        } catch (DataIntegrityViolationException e) {
            throw new SkuDuplicadoException("Ya existe un producto con el SKU: " + dto.getCodigo());
        }
    }

    @Transactional
    public void desactivar(Long id) {
        Producto p = obtenerEntidad(id);
        boolean tieneStock = inventarioClient.obtenerInventariosPorProducto(id)
                .stream()
                .anyMatch(inv -> inv.getStockActual() > 0);
        if (tieneStock) {
            throw new ProductoConStockException(
                    "No se puede desactivar el producto id=" + id + " porque tiene stock activo en una o más bodegas.");
        }
        p.setActivo(false);
        repository.save(p);
        
    }

    @Transactional
    public void activar(Long id) {
        Producto p = obtenerEntidad(id);
        p.setActivo(true);
        repository.save(p);
        
    }

    @Transactional
    public void eliminar(Long id) {
        repository.delete(obtenerEntidad(id));
        
    }

    private void validarPrecioBase(Double precioBase) {
        if (precioBase == null || precioBase <= 0) {
            throw new ProductoValidacionException("El precio base debe ser mayor a 0.");
        }
    }

    private Producto obtenerEntidad(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con id: " + id));
    }

    private ProductoResponseDTO mapearADTO(Producto p) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(p.getId());
        dto.setCodigo(p.getCodigo());
        dto.setNombre(p.getNombre());
        dto.setPrecioBase(p.getPrecioBase());
        dto.setProveedorId(p.getProveedorId());
        dto.setActivo(p.getActivo());
        return dto;
    }
}
