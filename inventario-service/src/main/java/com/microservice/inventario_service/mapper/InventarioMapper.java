package com.microservice.inventario_service.mapper;

import com.microservice.inventario_service.dto.InventarioRequestDTO;
import com.microservice.inventario_service.dto.InventarioResponseDTO;
import com.microservice.inventario_service.model.Inventario;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventarioMapper {

    /**
     * Convierte InventarioRequestDTO a Inventario (para crear/actualizar)
     */
    public Inventario toEntity(InventarioRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Inventario inventario = new Inventario();
        inventario.setProductoId(dto.getProductoId());
        inventario.setBodegaId(dto.getBodegaId());
        inventario.setStockActual(dto.getStockActual());
        inventario.setStockMinimo(dto.getStockMinimo());
        inventario.setStockMaximo(dto.getStockMaximo());
        inventario.setPuntoReposicion(dto.getPuntoReposicion());
        inventario.setUltimaActualizacion(LocalDateTime.now());

        return inventario;
    }

    /**
     * Convierte Inventario a InventarioResponseDTO (para devolver en respuestas)
     */
    public InventarioResponseDTO toResponseDTO(Inventario inventario) {
        if (inventario == null) {
            return null;
        }

        InventarioResponseDTO dto = new InventarioResponseDTO();
        dto.setId(inventario.getId());
        dto.setProductoId(inventario.getProductoId());
        dto.setBodegaId(inventario.getBodegaId());
        dto.setStockActual(inventario.getStockActual());
        dto.setStockMinimo(inventario.getStockMinimo());
        dto.setStockMaximo(inventario.getStockMaximo());
        dto.setPuntoReposicion(inventario.getPuntoReposicion());
        dto.setUltimaActualizacion(inventario.getUltimaActualizacion());

        return dto;
    }

    /**
     * Convierte una lista de Inventario a lista de InventarioResponseDTO
     */
    public List<InventarioResponseDTO> toResponseDTOList(List<Inventario> inventarios) {
        if (inventarios == null) {
            return null;
        }

        return inventarios.stream()
            .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza una entidad Inventario existente a partir de InventarioRequestDTO
     */
    public void updateEntity(InventarioRequestDTO dto, Inventario inventario) {
        if (dto == null || inventario == null) {
            return;
        }

        inventario.setProductoId(dto.getProductoId());
        inventario.setBodegaId(dto.getBodegaId());
        inventario.setStockActual(dto.getStockActual());
        inventario.setStockMinimo(dto.getStockMinimo());
        inventario.setStockMaximo(dto.getStockMaximo());
        inventario.setPuntoReposicion(dto.getPuntoReposicion());
        inventario.setUltimaActualizacion(LocalDateTime.now());
    }
}
