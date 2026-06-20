package com.microservice.bodega_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.bodega_service.dto.BodegaRequest;
import com.microservice.bodega_service.dto.BodegaResponse;
import com.microservice.bodega_service.exception.ManejadorGlobal.BodegaNotFoundException;
import com.microservice.bodega_service.exception.ManejadorGlobal.BodegaNombreDuplicadoException;
import com.microservice.bodega_service.model.Bodega;
import com.microservice.bodega_service.repository.BodegaRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class BodegaService {

    private final BodegaRepository repository;

    @Transactional(readOnly = true)
    public List<BodegaResponse> listar() {
        
        List<Bodega> listaBodegas = repository.findAll();
        List<BodegaResponse> listaResponse = new ArrayList<>();

        for (Bodega bodega : listaBodegas){
            BodegaResponse response = mapear(bodega);
            listaResponse.add(response);
        }
        return listaResponse;
    }

    @Transactional
    public BodegaResponse guardar(BodegaRequest request) {
        if (repository.existsByNombre(request.getNombre())) {
            throw new BodegaNombreDuplicadoException("El nombre de la bodega ya está registrado: " + request.getNombre());
        }

        Bodega bodega = new Bodega();
        bodega.setNombre(request.getNombre());
        bodega.setUbicacion(request.getUbicacion());
        bodega.setCapacidadMaximaItems(request.getCapacidadMaximaItems());

        Bodega guardada = repository.save(bodega);
        return mapear(guardada);
    }

    @Transactional(readOnly = true)
    public BodegaResponse buscarPorId(Long id) {
        Bodega bodega = repository.findById(id)
                .orElseThrow(() -> new BodegaNotFoundException("Bodega no encontrada con id: " + id));
        return mapear(bodega);
    }

    @Transactional
    public void eliminar(Long id) {
        Bodega bodega = repository.findById(id)
                .orElseThrow(() -> new BodegaNotFoundException("Bodega no encontrada con id: " + id));
        repository.delete(bodega);
    }

    private BodegaResponse mapear(Bodega bodega) {
        BodegaResponse response = new BodegaResponse();
        response.setId(bodega.getId());
        response.setNombre(bodega.getNombre());
        response.setUbicacion(bodega.getUbicacion());
        response.setCapacidadMaximaItems(bodega.getCapacidadMaximaItems());
        return response;
    }
}
