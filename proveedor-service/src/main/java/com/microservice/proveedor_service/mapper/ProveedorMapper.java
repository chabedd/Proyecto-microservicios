package com.microservice.proveedor_service.mapper;

import org.springframework.stereotype.Component;

import com.microservice.proveedor_service.dto.ProveedorRequestDTO;
import com.microservice.proveedor_service.dto.ProveedorResponseDTO;
import com.microservice.proveedor_service.model.Proveedor;

@Component
public class ProveedorMapper {

    public Proveedor toEntity(ProveedorRequestDTO dto) {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(dto.getNombre());
        proveedor.setRut(dto.getRut());
        proveedor.setEmail(dto.getEmail());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setDireccion(dto.getDireccion());
        return proveedor;
    }

    public ProveedorResponseDTO toDTO(Proveedor proveedor) {
        return new ProveedorResponseDTO(
                proveedor.getId(),
                proveedor.getNombre(),
                proveedor.getRut(),
                proveedor.getEmail(),
                proveedor.getTelefono(),
                proveedor.getDireccion(),
                proveedor.getActivo(),
                proveedor.getFechaRegistro());
    }

}
