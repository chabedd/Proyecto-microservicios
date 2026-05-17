package com.microservice.abastecimiento_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.abastecimiento_service.dto.DetalleOrdenCompraRequestDTO;
import com.microservice.abastecimiento_service.dto.DetalleOrdenCompraResponseDTO;
import com.microservice.abastecimiento_service.dto.OrdenCompraRequestDTO;
import com.microservice.abastecimiento_service.dto.OrdenCompraResponseDTO;
import com.microservice.abastecimiento_service.dto.ProductoResponseDTO;
import com.microservice.abastecimiento_service.exception.ManejadorGlobal.RecursoNoEncontradoException;
import com.microservice.abastecimiento_service.exception.ManejadorGlobal.ReglaDeNegocioException;
import com.microservice.abastecimiento_service.exception.ManejadorGlobal.ValidacionException;
import com.microservice.abastecimiento_service.feignclient.AjusteStockDTO;
import com.microservice.abastecimiento_service.feignclient.InventarioClient;
import com.microservice.abastecimiento_service.feignclient.ProductoClient;
import com.microservice.abastecimiento_service.feignclient.ProveedorClient;
import com.microservice.abastecimiento_service.feignclient.ProveedorResponseDTO;
import com.microservice.abastecimiento_service.model.DetalleOrdenCompra;
import com.microservice.abastecimiento_service.model.OrdenCompra;
import com.microservice.abastecimiento_service.model.TipoEstado;
import com.microservice.abastecimiento_service.repository.OrdenCompraRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AbastecimientoService {

    private final OrdenCompraRepository repository;
    private final InventarioClient inventarioClient;
    private final ProductoClient productoClient;
    private final ProveedorClient proveedorClient;

    @Transactional
    public OrdenCompraResponseDTO crearOrden(OrdenCompraRequestDTO dto) {
        // Verificar proveedor activo
        ProveedorResponseDTO proveedor = proveedorClient.obtenerProveedorPorId(dto.getProveedorId());
        if (Boolean.FALSE.equals(proveedor.getActivo())) {
            throw new ReglaDeNegocioException("No se puede crear una OC para el proveedor id=" + dto.getProveedorId() + " porque está inactivo.");
        }

        // Verificar que cada producto en el detalle exista y esté activo
        for (DetalleOrdenCompraRequestDTO d : dto.getDetalles()) {
            ProductoResponseDTO producto = productoClient.obtenerProductoPorId(d.getProductoId());
            if (Boolean.FALSE.equals(producto.getActivo())) {
                throw new ReglaDeNegocioException("El producto id=" + d.getProductoId() + " está inactivo y no puede incluirse en una OC.");
            }
        }

        OrdenCompra oc = new OrdenCompra();
        oc.setProveedorId(dto.getProveedorId());
        oc.setEstado(TipoEstado.PENDIENTE);

        for (DetalleOrdenCompraRequestDTO d : dto.getDetalles()) {
            DetalleOrdenCompra detalle = new DetalleOrdenCompra();
            detalle.setProductoId(d.getProductoId());
            detalle.setCantidad(d.getCantidad());
            detalle.setPrecioUnitario(d.getPrecioUnitario());
            detalle.setOrdenCompra(oc);
            oc.getDetalles().add(detalle);
        }

        OrdenCompra guardada = repository.save(oc);
        log.info("OC creada id={} proveedorId={}", guardada.getId(), guardada.getProveedorId());
        return mapear(guardada);
    }

    @Transactional(readOnly = true)
    public List<OrdenCompraResponseDTO> obtenerTodas() {
        List<OrdenCompraResponseDTO> resultado = new ArrayList<>();
        for (OrdenCompra oc : repository.findAll()) {
            resultado.add(mapear(oc));
        }
        return resultado;
    }

    @Transactional(readOnly = true)
    public OrdenCompraResponseDTO obtenerPorId(Long id) {
        OrdenCompra oc = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("OC no encontrada con id: " + id));
        return mapear(oc);
    }

    @Transactional
    public OrdenCompraResponseDTO cambiarEstado(Long id, String nuevoEstado, Long bodegaId) {
        OrdenCompra oc = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("OC no encontrada con id: " + id));

        TipoEstado estadoNuevo;
        try {
            estadoNuevo = TipoEstado.valueOf(nuevoEstado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidacionException("Estado inválido: " + nuevoEstado + ". Valores aceptados: APROBADA, RECIBIDA, CANCELADA");
        }

        validarTransicion(oc.getEstado(), estadoNuevo);

        // Al recibir la OC, registrar ENTRADA en inventario por cada detalle
        if (estadoNuevo == TipoEstado.RECIBIDA) {
            if (bodegaId == null) {
                throw new ValidacionException("Se requiere el parámetro bodegaId para recibir una OC.");
            }
            for (DetalleOrdenCompra detalle : oc.getDetalles()) {
                inventarioClient.ajustarStock(
                        new AjusteStockDTO(detalle.getProductoId(), bodegaId, detalle.getCantidad()));
            }
        }

        oc.setEstado(estadoNuevo);
        OrdenCompra actualizada = repository.save(oc);
        log.info("OC id={} cambió a estado={}", id, estadoNuevo);
        return mapear(actualizada);
    }

    @Transactional
    public void cancelarOrden(Long id) {
        OrdenCompra oc = repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("OC no encontrada con id: " + id));
        validarTransicion(oc.getEstado(), TipoEstado.CANCELADA);
        oc.setEstado(TipoEstado.CANCELADA);
        repository.save(oc);
        log.info("OC cancelada id={}", id);
    }

    @Transactional(readOnly = true)
    public boolean tieneOrdenesActivasPorProveedor(Long proveedorId) {
        List<TipoEstado> estadosActivos = List.of(TipoEstado.PENDIENTE, TipoEstado.APROBADA);
        return repository.existsByProveedorIdAndEstadoIn(proveedorId, estadosActivos);
    }

    // PENDIENTE → APROBADA | CANCELADA
    // APROBADA  → RECIBIDA | CANCELADA
    // RECIBIDA y CANCELADA son estados terminales
    private void validarTransicion(TipoEstado actual, TipoEstado nuevo) {
        boolean valida = switch (actual) {
            case PENDIENTE -> nuevo == TipoEstado.APROBADA || nuevo == TipoEstado.CANCELADA;
            case APROBADA -> nuevo == TipoEstado.RECIBIDA || nuevo == TipoEstado.CANCELADA;
            case RECIBIDA, CANCELADA -> false;
        };
        if (!valida) {
            throw new ReglaDeNegocioException(
                    "Transición inválida: no se puede pasar de " + actual + " a " + nuevo + ".");
        }
    }

    private OrdenCompraResponseDTO mapear(OrdenCompra oc) {
        List<DetalleOrdenCompraResponseDTO> detallesDTO = new ArrayList<>();
        for (DetalleOrdenCompra d : oc.getDetalles()) {
            detallesDTO.add(new DetalleOrdenCompraResponseDTO(
                    d.getId(), d.getProductoId(), d.getCantidad(), d.getPrecioUnitario()));
        }
        return new OrdenCompraResponseDTO(
                oc.getId(), oc.getProveedorId(), oc.getEstado(), oc.getFechaCreacion(), detallesDTO);
    }
}
