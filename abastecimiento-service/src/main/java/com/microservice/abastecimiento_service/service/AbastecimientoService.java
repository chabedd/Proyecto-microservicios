package com.microservice.abastecimiento_service.service;


import java.util.ArrayList;
import java.util.List;


import org.springframework.stereotype.Service;

import com.microservice.abastecimiento_service.dto.AjusteStockDTO;
import com.microservice.abastecimiento_service.dto.OrdenCompraRequestDTO;
import com.microservice.abastecimiento_service.dto.OrdenCompraResponseDTO;
import com.microservice.abastecimiento_service.feignclient.InventarioClient;


import com.microservice.abastecimiento_service.model.OrdenCompra;
import com.microservice.abastecimiento_service.model.TipoEstado;
import com.microservice.abastecimiento_service.repository.OrdenCompraRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor
public class AbastecimientoService {
    private final OrdenCompraRepository repository; 
    private final InventarioClient inventarioClient;                                                                                  


    public OrdenCompraResponseDTO crearOrden(OrdenCompraRequestDTO dto){
        OrdenCompra oc = new OrdenCompra();
        oc.setProductoId(dto.getProductoId());
        oc.setCantidad(dto.getCantidad());
        oc.setEstado(TipoEstado.PENDIENTE);
        return mapear(repository.save(oc));
    }
    
    public List<OrdenCompraResponseDTO> obtenerTodas() { 
        List<OrdenCompra> listaOC =repository.findAll();
        List<OrdenCompraResponseDTO> ocResp = new ArrayList<>();
        for (OrdenCompra ordenCompra : listaOC) {
            ocResp.add(mapear(ordenCompra));    
        }
        return ocResp; 
    }
    public OrdenCompraResponseDTO obtenerPorId(Long id) { 
        return mapear(repository.findById(id).orElseThrow(() -> new RuntimeException("OC no encontrada"))); }

    // Actualizar = Cambiar Estado de la Orden
    public OrdenCompraResponseDTO cambiarEstado(Long id, String nuevoEstado) {
        OrdenCompra oc = repository.findById(id).orElseThrow(() -> new RuntimeException("OC no encontrada"));
        try { 
            oc.setEstado(TipoEstado.valueOf(nuevoEstado.toUpperCase())); 
        } 
            catch (Exception e) { throw new RuntimeException("Estado inválido");

            }
        return mapear(repository.save(oc));
    }

    // Eliminar = Cancelar Lógicamente
    public void cancelarOrden(Long id) {
        OrdenCompra oc = repository.findById(id).orElseThrow(() -> new RuntimeException("OC no encontrada"));
        oc.setEstado(TipoEstado.CANCELADA);
        repository.save(oc);
    }

    private OrdenCompraResponseDTO mapear(OrdenCompra oc) {
        OrdenCompraResponseDTO dto = new OrdenCompraResponseDTO(); 
        dto.setId(oc.getId);
        dto.setCantidad(oc.getCantidad()); 
        dto.setEstado();
        dto.setProductoId(oc.getProductoId);
        return dto;
    
    }
    private boolean abastecimiento(AjusteStockDTO dto){
        OrdenCompra oc = repository.findById(dto.getOrdenCompraId()).orElseThrow(() -> new RuntimeException("Orden de compra no encontrada"));
        if (oc.getEstado()==TipoEstado.PENDIENTE){
            inventarioClient.ajustarStock(new AjusteStockDTO(oc.getProductoId(),dto.getBodegaId(),oc.getCantidad()));
            oc.setEstado(TipoEstado.APROBADA);
            return true;

            
        }
        return false;


    }

}
