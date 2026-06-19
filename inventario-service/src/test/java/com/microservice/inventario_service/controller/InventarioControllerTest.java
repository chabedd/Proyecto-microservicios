package com.microservice.inventario_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.inventario_service.dto.AjusteStockRequestDto;
import com.microservice.inventario_service.dto.InventarioRequestDTO;
import com.microservice.inventario_service.dto.InventarioResponseDTO;
import com.microservice.inventario_service.exception.ManejadorGlobal.InventarioNoEncontradoException;
import com.microservice.inventario_service.service.InventarioService;

@WebMvcTest(InventarioController.class)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false"
})
class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InventarioService inventarioService;

    // ── crearInventario ───────────────────────────────────────────────────────

    @Test
    void crearInventario_DatosValidos_RetornaCreated() throws Exception {
        InventarioRequestDTO request = new InventarioRequestDTO(10L, 20L, 100);
        InventarioResponseDTO response = new InventarioResponseDTO();
        response.setId(1L);
        response.setProductoId(10L);
        response.setBodegaId(20L);
        response.setStockActual(100);

        when(inventarioService.crearInventario(any(InventarioRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/inventarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.productoId").value(10L))
                .andExpect(jsonPath("$.bodegaId").value(20L))
                .andExpect(jsonPath("$.stockActual").value(100));
    }

    @Test
    void crearInventario_DatosInvalidos_RetornaBadRequest() throws Exception {
        InventarioRequestDTO request = new InventarioRequestDTO(null, 20L, -5);

        mockMvc.perform(post("/api/inventarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ── obtenerTodosInventarios ───────────────────────────────────────────────

    @Test
    void obtenerTodosInventarios_RetornaLista() throws Exception {
        InventarioResponseDTO response = new InventarioResponseDTO();
        response.setId(1L);
        response.setProductoId(10L);
        response.setBodegaId(20L);
        response.setStockActual(100);

        when(inventarioService.obtenerTodosInventarios()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/inventarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ── obtenerInventarioPorId ────────────────────────────────────────────────

    @Test
    void obtenerInventarioPorId_Existente_RetornaOk() throws Exception {
        InventarioResponseDTO response = new InventarioResponseDTO();
        response.setId(1L);
        response.setProductoId(10L);
        response.setBodegaId(20L);
        response.setStockActual(100);

        when(inventarioService.obtenerInventarioPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/inventarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void obtenerInventarioPorId_NoExistente_RetornaNotFound() throws Exception {
        when(inventarioService.obtenerInventarioPorId(99L)).thenThrow(new InventarioNoEncontradoException("Inventario no encontrado con id: 99"));

        mockMvc.perform(get("/api/inventarios/99"))
                .andExpect(status().isNotFound());
    }

    // ── obtenerInventariosPorProducto ─────────────────────────────────────────

    @Test
    void obtenerInventariosPorProducto_RetornaLista() throws Exception {
        InventarioResponseDTO response = new InventarioResponseDTO();
        response.setId(1L);
        response.setProductoId(10L);
        response.setBodegaId(20L);
        response.setStockActual(100);

        when(inventarioService.obtenerInventariosPorProducto(10L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/inventarios/producto/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productoId").value(10L));
    }

    // ── obtenerInventariosPorBodega ───────────────────────────────────────────

    @Test
    void obtenerInventariosPorBodega_RetornaLista() throws Exception {
        InventarioResponseDTO response = new InventarioResponseDTO();
        response.setId(1L);
        response.setProductoId(10L);
        response.setBodegaId(20L);
        response.setStockActual(100);

        when(inventarioService.obtenerInventariosPorBodega(20L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/inventarios/bodega/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bodegaId").value(20L));
    }

    // ── ajustarStockPost ──────────────────────────────────────────────────────

    @Test
    void ajustarStockPost_DatosValidos_RetornaOk() throws Exception {
        AjusteStockRequestDto request = new AjusteStockRequestDto(10L, 20L, 50);
        InventarioResponseDTO response = new InventarioResponseDTO();
        response.setId(1L);
        response.setProductoId(10L);
        response.setBodegaId(20L);
        response.setStockActual(150);

        when(inventarioService.ajustarStock(10L, 20L, 50)).thenReturn(response);

        mockMvc.perform(post("/api/inventarios/ajustar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockActual").value(150));
    }

    @Test
    void ajustarStockPost_DatosInvalidos_RetornaBadRequest() throws Exception {
        AjusteStockRequestDto request = new AjusteStockRequestDto(null, 20L, 50);

        mockMvc.perform(post("/api/inventarios/ajustar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ── eliminarInventario ────────────────────────────────────────────────────

    @Test
    void eliminarInventario_Existente_RetornaOk() throws Exception {
        doNothing().when(inventarioService).eliminarInventario(1L);

        mockMvc.perform(delete("/api/inventarios/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Inventario eliminado exitosamente"));
    }

    @Test
    void eliminarInventario_NoExistente_RetornaNotFound() throws Exception {
        doThrow(new InventarioNoEncontradoException("Inventario no encontrado con id: 99")).when(inventarioService).eliminarInventario(99L);

        mockMvc.perform(delete("/api/inventarios/99"))
                .andExpect(status().isNotFound());
    }
}
