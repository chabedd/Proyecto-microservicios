package com.microservice.proveedor_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.proveedor_service.dto.ProveedorRequestDTO;
import com.microservice.proveedor_service.dto.ProveedorResponseDTO;
import com.microservice.proveedor_service.exception.ManejadorGlobal.ProveedorNoEncontradoException;
import com.microservice.proveedor_service.exception.ManejadorGlobal.RutDuplicadoException;
import com.microservice.proveedor_service.mapper.ProveedorMapper;
import com.microservice.proveedor_service.model.Proveedor;
import com.microservice.proveedor_service.repository.ProveedorRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;

    public ProveedorResponseDTO crearProveedor(ProveedorRequestDTO dto) {
        Proveedor entidad = proveedorMapper.toEntity(dto);
        try {
            Proveedor saved = proveedorRepository.save(entidad);
            log.info("Proveedor creado id={} rut={}", saved.getId(), saved.getRut());
            return proveedorMapper.toDTO(saved);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad al crear proveedor (posible RUT duplicado): {}", e.getMessage());
            throw new RutDuplicadoException("Este RUT ya está registrado en el sistema. Por favor, verifica el RUT e intenta nuevamente.", e);
        }
    }

    @Transactional(readOnly = true)
    public ProveedorResponseDTO obtenerProveedorPorId(Long id) {
        Proveedor p = proveedorRepository.findById(id)
                .orElseThrow(() -> new ProveedorNoEncontradoException(obtenerMensajeErrorProveedor(id)));
        return proveedorMapper.toDTO(p);
    }

    @Transactional(readOnly = true)
    public List<ProveedorResponseDTO> obtenerTodosProveedores() {
        return proveedorRepository.findAll().stream()
                .map(proveedorMapper::toDTO)
                .toList();
    }



    public ProveedorResponseDTO actualizarProveedor(Long id, ProveedorRequestDTO proveedorRequest) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ProveedorNoEncontradoException(obtenerMensajeErrorProveedor(id)));

        proveedor.setNombre(proveedorRequest.getNombre());
        proveedor.setRut(proveedorRequest.getRut());
        proveedor.setEmail(proveedorRequest.getEmail());
        proveedor.setTelefono(proveedorRequest.getTelefono());
        proveedor.setDireccion(proveedorRequest.getDireccion());

        try {
            Proveedor saved = proveedorRepository.save(proveedor);
            log.info("Proveedor actualizado id={}", saved.getId());
            return proveedorMapper.toDTO(saved);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad al actualizar proveedor id={}: {}", id, e.getMessage());
            throw new RutDuplicadoException("El RUT ingresado ya existe en el sistema. Por favor, verifica los datos e intenta nuevamente.", e);
        }
    }



    public void eliminarProveedor(Long id) {
        if (!proveedorRepository.existsById(id)) {
            throw new ProveedorNoEncontradoException("Proveedor no encontrado con id: " + id);
        }
        proveedorRepository.deleteById(id);
        log.info("Proveedor eliminado id={}", id);
    }

    @Transactional(readOnly = true)
    public Optional<ProveedorResponseDTO> obtenerProveedorPorRut(String rut) {
        return proveedorRepository.findByRut(rut).map(proveedorMapper::toDTO);
    }

    private String obtenerMensajeErrorProveedor(Long id) {
        return "El proveedor con ID " + id + " no existe. Por favor, verifica el número e intenta nuevamente.";
    }
}
