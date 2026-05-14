package com.microservice.bodega_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.microservice.bodega_service.dto.BodegaRequest;
import com.microservice.bodega_service.dto.BodegaResponse;
import com.microservice.bodega_service.model.Bodega;
import com.microservice.bodega_service.repository.BodegaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BodegaService {

    private final BodegaRepository repository;

    public List<BodegaResponse> listar() {

        List<Bodega> listaEntity = repository.findAll();

        List<BodegaResponse> listaResponse = new ArrayList<>();

        for (Bodega bodega : listaEntity) {

            BodegaResponse response = new BodegaResponse();

            response.setId(bodega.getId());
            response.setNombre(bodega.getNombre());
            response.setUbicacion(bodega.getUbicacion());
            response.setCapacidad(bodega.getCapacidad());
            response.setActiva(bodega.getActiva());

            listaResponse.add(response);
        }

        return listaResponse;
    }

    public BodegaResponse guardar(BodegaRequest request) {

        Bodega bodega = new Bodega();

        bodega.setNombre(request.getNombre());
        bodega.setUbicacion(request.getUbicacion());
        bodega.setCapacidad(request.getCapacidad());
        bodega.setActiva(request.getActiva());

        Bodega guardada = repository.save(bodega);

        BodegaResponse response = new BodegaResponse();

        response.setId(guardada.getId());
        response.setNombre(guardada.getNombre());
        response.setUbicacion(guardada.getUbicacion());
        response.setCapacidad(guardada.getCapacidad());
        response.setActiva(guardada.getActiva());

        return response;
    }

    public BodegaResponse buscarPorId(Long id) {

        Bodega bodega = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Bodega no encontrada"));

        BodegaResponse response = new BodegaResponse();

        response.setId(bodega.getId());
        response.setNombre(bodega.getNombre());
        response.setUbicacion(bodega.getUbicacion());
        response.setCapacidad(bodega.getCapacidad());
        response.setActiva(bodega.getActiva());

        return response;
    }

    public void eliminar(Long id) {

        Bodega bodega = repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Bodega no encontrada"));

        repository.delete(bodega);
    }

}
