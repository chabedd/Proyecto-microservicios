package com.microservice.proveedor_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.proveedor_service.model.Proveedor;
import com.microservice.proveedor_service.repository.ProveedorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;


    public Proveedor crearProveedor(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    @Transactional(readOnly = true)
    public Proveedor obtenerProveedorPorId(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Proveedor> obtenerTodosProveedores() {
        return proveedorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Proveedor> obtenerProveedoresActivos() {
        return proveedorRepository.findByActivo(true);
    }

    public Proveedor actualizarProveedor(Long id, Proveedor proveedorActualizado) {
        Proveedor proveedor = obtenerProveedorPorId(id);

        proveedor.setNombre(proveedorActualizado.getNombre());
        proveedor.setEmail(proveedorActualizado.getEmail());
        proveedor.setTelefono(proveedorActualizado.getTelefono());
        proveedor.setDireccion(proveedorActualizado.getDireccion());
        proveedor.setActivo(proveedorActualizado.getActivo());

        return proveedorRepository.save(proveedor);
    }

    public void desactivarProveedor(Long id) {
        Proveedor proveedor = obtenerProveedorPorId(id);
        proveedor.setActivo(false);
        proveedorRepository.save(proveedor);
    }

    public void activarProveedor(Long id) {
        Proveedor proveedor = obtenerProveedorPorId(id);
        proveedor.setActivo(true);
        proveedorRepository.save(proveedor);
    }
    public void eliminarProveedor(Long id) {
        if (!proveedorRepository.existsById(id)) {
            throw new RuntimeException("Proveedor no encontrado con id: " + id);
        }
// aun sin terminar falta aplicar las reglas de negocio
        proveedorRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Proveedor> obtenerProveedorPorRut(String rut) {
        return proveedorRepository.findByRut(rut);
    }



}
