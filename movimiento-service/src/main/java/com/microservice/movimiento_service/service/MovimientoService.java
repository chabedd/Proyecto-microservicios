package com.microservice.movimiento_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.movimiento_service.dto.MovimientoRequestDTO;
import com.microservice.movimiento_service.dto.MovimientoResponseDTO;
import com.microservice.movimiento_service.feignClient.AjusteStockDTO;
import com.microservice.movimiento_service.feignClient.InventarioClient;
import com.microservice.movimiento_service.model.Movimiento;
import com.microservice.movimiento_service.model.TipoMovimiento;
import com.microservice.movimiento_service.repository.MovimientoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MovimientoService {

    private final MovimientoRepository repository;
    private final InventarioClient inventarioClient;

    public MovimientoResponseDTO registrar(MovimientoRequestDTO dto) {
        validarCamposBodega(dto.getTipo(), dto.getBodegaOrigenId(), dto.getBodegaDestinoId());

        Movimiento m = new Movimiento();
        m.setProductoId(dto.getProductoId());
        m.setBodegaOrigenId(dto.getBodegaOrigenId());
        m.setBodegaDestinoId(dto.getBodegaDestinoId());
        m.setTipo(dto.getTipo());
        m.setCantidad(dto.getCantidad());
        m.setMotivo(dto.getMotivo());

        Movimiento guardado = repository.save(m);
        aplicarEfectoInventario(guardado);

        return mapearADTO(guardado);
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> obtenerTodos() {
        List<Movimiento> lista = repository.findAll();
        List<MovimientoResponseDTO> resultado = new ArrayList<>();
        for (Movimiento m : lista) {
            resultado.add(mapearADTO(m));
        }
        return resultado;
    }

    @Transactional(readOnly = true)
    public MovimientoResponseDTO obtenerPorId(Long id) {
        return mapearADTO(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con id: " + id)));
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> obtenerPorProducto(Long productoId) {
        List<Movimiento> lista = repository.findByProductoId(productoId);
        List<MovimientoResponseDTO> resultado = new ArrayList<>();
        for (Movimiento m : lista) {
            resultado.add(mapearADTO(m));
        }
        return resultado;
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> obtenerPorBodega(Long bodegaId) {
        List<Movimiento> lista = repository.findByBodegaOrigenId(bodegaId);
        List<MovimientoResponseDTO> resultado = new ArrayList<>();
        for (Movimiento m : lista) {
            resultado.add(mapearADTO(m));
        }
        return resultado;
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponseDTO> obtenerPorTipo(TipoMovimiento tipo) {
        List<Movimiento> lista = repository.findByTipo(tipo);
        List<MovimientoResponseDTO> resultado = new ArrayList<>();
        for (Movimiento m : lista) {
            resultado.add(mapearADTO(m));
        }
        return resultado;
    }

    public MovimientoResponseDTO actualizar(Long id, MovimientoRequestDTO dto) {
        Movimiento existente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con id: " + id));

        validarCamposBodega(dto.getTipo(), dto.getBodegaOrigenId(), dto.getBodegaDestinoId());

        revertirEfectoInventario(existente);

        existente.setProductoId(dto.getProductoId());
        existente.setBodegaOrigenId(dto.getBodegaOrigenId());
        existente.setBodegaDestinoId(dto.getBodegaDestinoId());
        existente.setTipo(dto.getTipo());
        existente.setCantidad(dto.getCantidad());
        existente.setMotivo(dto.getMotivo());

        Movimiento actualizado = repository.save(existente);
        aplicarEfectoInventario(actualizado);

        return mapearADTO(actualizado);
    }

    public void eliminar(Long id) {
        Movimiento m = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con id: " + id));
        revertirEfectoInventario(m);
        repository.delete(m);
    }

    private void validarCamposBodega(TipoMovimiento tipo, Long origenId, Long destinoId) {
        switch (tipo) {
            case ENTRADA:
                if (destinoId == null)
                    throw new RuntimeException("El tipo ENTRADA requiere bodegaDestinoId");
                break;
            case SALIDA:
                if (origenId == null)
                    throw new RuntimeException("El tipo SALIDA requiere bodegaOrigenId");
                break;
            case TRANSFERENCIA:
                if (origenId == null || destinoId == null)
                    throw new RuntimeException("El tipo TRANSFERENCIA requiere bodegaOrigenId y bodegaDestinoId");
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
                inventarioClient.ajustarStock(
                        new AjusteStockDTO(m.getProductoId(), m.getBodegaOrigenId(), -m.getCantidad()));
                inventarioClient.ajustarStock(
                        new AjusteStockDTO(m.getProductoId(), m.getBodegaDestinoId(), m.getCantidad()));
                break;
        }
    }

    private void revertirEfectoInventario(Movimiento m) {
        switch (m.getTipo()) {
            case ENTRADA:
                inventarioClient.ajustarStock(
                        new AjusteStockDTO(m.getProductoId(), m.getBodegaDestinoId(), -m.getCantidad()));
                break;
            case SALIDA:
                inventarioClient.ajustarStock(
                        new AjusteStockDTO(m.getProductoId(), m.getBodegaOrigenId(), m.getCantidad()));
                break;
            case TRANSFERENCIA:
                inventarioClient.ajustarStock(
                        new AjusteStockDTO(m.getProductoId(), m.getBodegaOrigenId(), m.getCantidad()));
                inventarioClient.ajustarStock(
                        new AjusteStockDTO(m.getProductoId(), m.getBodegaDestinoId(), -m.getCantidad()));
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
