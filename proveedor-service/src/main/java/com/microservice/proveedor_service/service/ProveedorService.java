package com.microservice.proveedor_service.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.proveedor_service.dto.ProveedorRequestDTO;
import com.microservice.proveedor_service.dto.ProveedorResponseDTO;
import com.microservice.proveedor_service.exception.ManejadorGlobal.ProveedorNoEncontradoException;
import com.microservice.proveedor_service.exception.ManejadorGlobal.ProveedorPersistenciaException;
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
        } catch (DataAccessException e) {
            log.error("Error de acceso a datos al crear proveedor: {}", e.getMessage());
            throw new ProveedorPersistenciaException("No pudimos guardar el proveedor. Por favor, intenta nuevamente en unos momentos.", e);
        }
    }

    @Transactional(readOnly = true)
    public ProveedorResponseDTO obtenerProveedorPorId(Long id) {
        try {
            Proveedor p = proveedorRepository.findById(id)
                    .orElseThrow(() -> new ProveedorNoEncontradoException(obtenerMensajeErrorProveedor(id)));
            return proveedorMapper.toDTO(p);
        } catch (DataAccessException e) {
            log.error("Error al obtener proveedor id={}: {}", id, e.getMessage());
            throw new ProveedorPersistenciaException("No pudimos recuperar la información. Por favor, intenta nuevamente.", e);
        }
    }

    @Transactional(readOnly = true)
    public List<ProveedorResponseDTO> obtenerTodosProveedores() {
        try {
            List<Proveedor> proveedores = proveedorRepository.findAll();
            return proveedores.stream().map(proveedorMapper::toDTO).collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Error al obtener todos los proveedores: {}", e.getMessage());
            throw new ProveedorPersistenciaException("No pudimos cargar la lista de proveedores. Por favor, intenta nuevamente.", e);
        }
    }

    @Transactional(readOnly = true)
    public List<ProveedorResponseDTO> obtenerProveedoresActivos() {
        try {
            List<Proveedor> activos = proveedorRepository.findByActivo(true);
            return activos.stream().map(proveedorMapper::toDTO).collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Error al obtener proveedores activos: {}", e.getMessage());
            throw new ProveedorPersistenciaException("No pudimos cargar los proveedores activos. Por favor, intenta nuevamente.", e);
        }
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
        } catch (DataAccessException e) {
            log.error("Error al acceder a la base de datos en actualización id={}: {}", id, e.getMessage());
            throw new ProveedorPersistenciaException("No pudimos actualizar el proveedor. Por favor, intenta nuevamente.", e);
        }
    }

    public void desactivarProveedor(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ProveedorNoEncontradoException(obtenerMensajeErrorProveedor(id)));
        try {
            proveedor.setActivo(false);
            proveedorRepository.save(proveedor);
            log.info("Proveedor desactivado id={}", id);
        } catch (DataAccessException e) {
            log.error("Error al desactivar proveedor id={}: {}", id, e.getMessage());
            throw new ProveedorPersistenciaException("No pudimos desactivar el proveedor. Por favor, intenta nuevamente.", e);
        }
    }

    public void activarProveedor(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ProveedorNoEncontradoException(obtenerMensajeErrorProveedor(id)));
        try {
            proveedor.setActivo(true);
            proveedorRepository.save(proveedor);
            log.info("Proveedor activado id={}", id);
        } catch (DataAccessException e) {
            log.error("Error al activar proveedor id={}: {}", id, e.getMessage());
            throw new ProveedorPersistenciaException("No pudimos activar el proveedor. Por favor, intenta nuevamente.", e);
        }
    }

    public void eliminarProveedor(Long id) {
        if (!proveedorRepository.existsById(id)) {
            throw new ProveedorNoEncontradoException("Proveedor no encontrado con id: " + id);
        }
        try {
            proveedorRepository.deleteById(id);
            log.info("Proveedor eliminado id={}", id);
        } catch (DataAccessException e) {
            log.error("Error al eliminar proveedor id={}: {}", id, e.getMessage());
            throw new ProveedorPersistenciaException("No pudimos eliminar el proveedor. Por favor, intenta nuevamente.", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<ProveedorResponseDTO> obtenerProveedorPorRut(String rut) {
        try {
            Optional<Proveedor> proveedor = proveedorRepository.findByRut(rut);
            return proveedor.map(proveedorMapper::toDTO);
        } catch (DataAccessException e) {
            log.error("Error al obtener proveedor por rut={}: {}", rut, e.getMessage());
            throw new ProveedorPersistenciaException("No pudimos buscar el proveedor. Por favor, intenta nuevamente.", e);
        }
    }

    private String obtenerMensajeErrorProveedor(Long id) {
        return "El proveedor con ID " + id + " no existe. Por favor, verifica el número e intenta nuevamente.";
    }



}
