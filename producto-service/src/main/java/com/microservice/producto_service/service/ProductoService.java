package com.microservice.producto_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.microservice.producto_service.dto.ProductoRequestDTO;
import com.microservice.producto_service.dto.ProductoResponseDTO;
import com.microservice.producto_service.feignClient.ProveedorClient;
import com.microservice.producto_service.model.Producto;
import com.microservice.producto_service.repository.ProductoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository repository;
    private final ProveedorClient proveedorClient;

    @Transactional
    public ProductoResponseDTO crear(ProductoRequestDTO dto) {
        Producto p = new Producto();
        p.setCodigo(dto.getCodigo()); 
        p.setNombre(dto.getNombre()); 
        p.setPrecioBase(dto.getPrecioBase()); 
        p.setProveedorId(dto.getProveedorId());
        return mapearADTO(repository.save(p));
    }
    @Transactional
    public List<ProductoResponseDTO> obtenerTodos(){
        List<Producto> listaProducto = repository.findAll();
        List<ProductoResponseDTO> lista = new ArrayList<>();
        for (Producto prod : listaProducto) {
            lista.add(mapearADTO(prod));
        }
        return lista;
    }
        
    @Transactional
    public ProductoResponseDTO obtenerPorId(Long id) { 
        return mapearADTO(repository.findById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado"))); }
    @Transactional    
    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO dto) {
        Producto p = repository.findById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        p.setCodigo(dto.getCodigo());
        p.setNombre(dto.getNombre()); 
        p.setPrecioBase(dto.getPrecioBase()); 
        p.setProveedorId(dto.getProveedorId());
        return mapearADTO(repository.save(p));
    }
    @Transactional
    public void eliminar(Long id) { 
        repository.delete(repository.findById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado"))); }

    
    @Transactional
    private ProductoResponseDTO mapearADTO(Producto p) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(p.getId());
        dto.setCodigo(p.getCodigo()); 
        dto.setNombre(p.getNombre()); 
        dto.setPrecioBase(p.getPrecioBase()); 
        dto.setProveedorId(p.getProveedorId());
        return dto;
    }
     
 

}
