package com.microservice.bodega_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.bodega_service.dto.BodegaRequest;
import com.microservice.bodega_service.dto.BodegaResponse;
import com.microservice.bodega_service.service.BodegaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bodegas")
@RequiredArgsConstructor
public class BodegaController {

    private final BodegaService service;

    @GetMapping("/")
    public ResponseEntity<List<BodegaResponse>> listar() {

        List<BodegaResponse> lista = service.listar();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BodegaResponse> buscarPorId(
            @PathVariable Long id) {

        BodegaResponse response = service.buscarPorId(id);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BodegaResponse> guardar(
            @Valid @RequestBody BodegaRequest request) {

        BodegaResponse response = service.guardar(request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.noContent().build();
    }

}
