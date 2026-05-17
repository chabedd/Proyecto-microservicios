package com.microservice.movimiento_service.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.movimiento_service.dto.MovimientoRequestDTO;
import com.microservice.movimiento_service.dto.MovimientoResponseDTO;
import com.microservice.movimiento_service.exception.ManejadorGlobal.MovimientoNoEncontradoException;
import com.microservice.movimiento_service.exception.ManejadorGlobal.ValidacionMovimientoException;
import com.microservice.movimiento_service.feignClient.AjusteStockDTO;
import com.microservice.movimiento_service.feignClient.InventarioClient;
import com.microservice.movimiento_service.model.Movimiento;
import com.microservice.movimiento_service.model.TipoMovimiento;
import com.microservice.movimiento_service.repository.MovimientoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovimientoService {

    private final MovimientoRepository repository;
    private final InventarioClient inventarioClient;

    @Transactional
    public MovimientoResponseDTO registrar(MovimientoRequestDTO dto) {
        validarCamposBodega(dto.getTipo(), dto.getBodegaOrigenId(), dto.getBodegaDestinoId());

        Movimiento m = new Movimiento();
        m.setProductoId(dto.getProductoId());
        m.setBodegaOrigenId(dto.getBodegaOrigenId());
        m.setBodegaDestinoId(dto.getBodegaDestinoId());
        m.setTipo(dto.getTipo());
        m.setCantidad(dto.getCantidad());
        m.setMotivo(dto.getMotivo());
        
        m.setFecha(LocalDateTime.now());

        Movimiento guardado = repository.save(m);
        aplicarEfectoInventario(guardado);

        log.info("Movimiento registrado id={} tipo={}", guardado.getId(), guardado.getTipo());
        return mapearADTO(guardado);
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> obtenerTodos() {
        List<MovimientoResponseDTO> resultado = new ArrayList<>();
        for (Movimiento m : repository.findAll()) {
            resultado.add(mapearADTO(m));
        }
        return resultado;
    }

    @Transactional(readOnly = true)
    public MovimientoResponseDTO obtenerPorId(Long id) {
        return mapearADTO(repository.findById(id)
                .orElseThrow(() -> new MovimientoNoEncontradoException("Movimiento no encontrado con id: " + id)));
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> obtenerPorProducto(Long productoId) {
        List<MovimientoResponseDTO> resultado = new ArrayList<>();
        for (Movimiento m : repository.findByProductoId(productoId)) {
            resultado.add(mapearADTO(m));
        }
        return resultado;
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> obtenerPorBodega(Long bodegaId) {
        List<MovimientoResponseDTO> resultado = new ArrayList<>();
        for (Movimiento m : repository.findByBodegaOrigenIdOrBodegaDestinoId(bodegaId, bodegaId)) {
            resultado.add(mapearADTO(m));
        }
        return resultado;
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> obtenerPorTipo(TipoMovimiento tipo) {
        List<MovimientoResponseDTO> resultado = new ArrayList<>();
        for (Movimiento m : repository.findByTipo(tipo)) {
            resultado.add(mapearADTO(m));
        }
        return resultado;
    }

    private void validarCamposBodega(TipoMovimiento tipo, Long origenId, Long destinoId) {
        switch (tipo) {
            case ENTRADA:
                if (destinoId == null)
                    throw new ValidacionMovimientoException("El tipo ENTRADA requiere bodegaDestinoId.");
                break;
            case SALIDA:
                if (origenId == null)
                    throw new ValidacionMovimientoException("El tipo SALIDA requiere bodegaOrigenId.");
                break;
            case TRANSFERENCIA:
                if (origenId == null || destinoId == null)
                    throw new ValidacionMovimientoException("El tipo TRANSFERENCIA requiere bodegaOrigenId y bodegaDestinoId.");
                if (origenId.equals(destinoId))
                    throw new ValidacionMovimientoException("La bodega origen y destino no pueden ser la misma en una TRANSFERENCIA.");
                break;
        }
    }

    private void aplicarEfectoInventario(Movimiento m) {
        switch (m.getTipo()) {
            case ENTRADA:
                inventarioClient.ajustarStock(
                        new AjusteStockDTO(m.getProductoId(), m.getBodegaDestinoId(), m.getCantidad()));
                break;
            case SALIDA:
                inventarioClient.ajustarStock(
                        new AjusteStockDTO(m.getProductoId(), m.getBodegaOrigenId(), -m.getCantidad()));
                break;
            case TRANSFERENCIA:
                // Paso 1: descontar en bodega origen
                inventarioClient.ajustarStock(
                        new AjusteStockDTO(m.getProductoId(), m.getBodegaOrigenId(), -m.getCantidad()));
                // Paso 2: sumar en bodega destino; si falla, compensar revirtiendo paso 1
                try {
                    inventarioClient.ajustarStock(
                            new AjusteStockDTO(m.getProductoId(), m.getBodegaDestinoId(), m.getCantidad()));
                } catch (Exception ex) {
                    log.error("Error al sumar stock en bodega destino id={}. Revirtiendo descuento en bodega origen.", m.getBodegaDestinoId());
                    inventarioClient.ajustarStock(
                            new AjusteStockDTO(m.getProductoId(), m.getBodegaOrigenId(), m.getCantidad()));
                    throw new ValidacionMovimientoException(
                            "No se pudo completar la transferencia: fallo al actualizar bodega destino. Se revirtió el descuento en bodega origen.");
                }
                break;
        }
    }

    private MovimientoResponseDTO mapearADTO(Movimiento m) {
        MovimientoResponseDTO dto = new MovimientoResponseDTO();
        dto.setId(m.getId());
        dto.setProductoId(m.getProductoId());
        dto.setBodegaOrigenId(m.getBodegaOrigenId());
        dto.setBodegaDestinoId(m.getBodegaDestinoId());
        dto.setTipo(m.getTipo());
        dto.setCantidad(m.getCantidad());
        dto.setMotivo(m.getMotivo());
        dto.setFecha(m.getFecha());
        return dto;
    }
}
