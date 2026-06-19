package com.microservice.proveedor_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.proveedor_service.dto.ProveedorRequestDTO;
import com.microservice.proveedor_service.dto.ProveedorResponseDTO;
import com.microservice.proveedor_service.exception.ManejadorGlobal.ProveedorNoEncontradoException;
import com.microservice.proveedor_service.exception.ManejadorGlobal.RutDuplicadoException;
import com.microservice.proveedor_service.service.ProveedorService;

@WebMvcTest(ProveedorController.class)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false"
})
class ProveedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProveedorService proveedorService;

    // ── crearProveedor ─────────────────────────────────────────────────────────

    @Test
    void crearProveedor_DatosValidos_RetornaCreated() throws Exception {
        ProveedorRequestDTO request = new ProveedorRequestDTO(
                "Proveedor Test", "12345678-9", "test@proveedor.com", "912345678", "Calle Test 123");
        ProveedorResponseDTO response = new ProveedorResponseDTO();
        response.setId(1L);
        response.setNombre("Proveedor Test");
        response.setRut("12345678-9");

        when(proveedorService.crearProveedor(any(ProveedorRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/proveedores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Proveedor Test"))
                .andExpect(jsonPath("$.rut").value("12345678-9"));
    }

    @Test
    void crearProveedor_DatosInvalidos_RetornaBadRequest() throws Exception {
        ProveedorRequestDTO request = new ProveedorRequestDTO(
                "", "12345678-9", "email-invalido", "123", "Calle Test 123");

        mockMvc.perform(post("/api/proveedores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearProveedor_RutDuplicado_RetornaConflict() throws Exception {
        ProveedorRequestDTO request = new ProveedorRequestDTO(
                "Proveedor Test", "12345678-9", "test@proveedor.com", "912345678", "Calle Test 123");

        when(proveedorService.crearProveedor(any(ProveedorRequestDTO.class)))
                .thenThrow(new RutDuplicadoException("12345678-9"));

        mockMvc.perform(post("/api/proveedores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    // ── obtenerTodosProveedores ────────────────────────────────────────────────

    @Test
    void obtenerTodosProveedores_RetornaLista() throws Exception {
        ProveedorResponseDTO response = new ProveedorResponseDTO();
        response.setId(1L);
        response.setNombre("Proveedor Test");
        response.setRut("12345678-9");

        when(proveedorService.obtenerTodosProveedores()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/proveedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ── obtenerProveedorPorId ──────────────────────────────────────────────────

    @Test
    void obtenerProveedorPorId_Existente_RetornaOk() throws Exception {
        ProveedorResponseDTO response = new ProveedorResponseDTO();
        response.setId(1L);
        response.setNombre("Proveedor Test");
        response.setRut("12345678-9");

        when(proveedorService.obtenerProveedorPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/proveedores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void obtenerProveedorPorId_NoExistente_RetornaNotFound() throws Exception {
        when(proveedorService.obtenerProveedorPorId(99L)).thenThrow(new ProveedorNoEncontradoException("Proveedor no encontrado con id: 99"));

        mockMvc.perform(get("/api/proveedores/99"))
                .andExpect(status().isNotFound());
    }

    // ── obtenerProveedorPorRut ─────────────────────────────────────────────────

    @Test
    void obtenerProveedorPorRut_Existente_RetornaOk() throws Exception {
        ProveedorResponseDTO response = new ProveedorResponseDTO();
        response.setId(1L);
        response.setNombre("Proveedor Test");
        response.setRut("12345678-9");

        when(proveedorService.obtenerProveedorPorRut("12345678-9")).thenReturn(Optional.of(response));

        mockMvc.perform(get("/api/proveedores/rut/12345678-9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rut").value("12345678-9"));
    }

    @Test
    void obtenerProveedorPorRut_NoExistente_RetornaNotFound() throws Exception {
        when(proveedorService.obtenerProveedorPorRut("99999999-9")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/proveedores/rut/99999999-9"))
                .andExpect(status().isNotFound());
    }

    // ── actualizarProveedor ────────────────────────────────────────────────────

    @Test
    void actualizarProveedor_DatosValidos_RetornaOk() throws Exception {
        ProveedorRequestDTO request = new ProveedorRequestDTO(
                "Proveedor Modificado", "12345678-9", "test@proveedor.com", "912345678", "Calle Test 123");
        ProveedorResponseDTO response = new ProveedorResponseDTO();
        response.setId(1L);
        response.setNombre("Proveedor Modificado");
        response.setRut("12345678-9");

        when(proveedorService.actualizarProveedor(eq(1L), any(ProveedorRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/proveedores/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Proveedor Modificado"));
    }

    @Test
    void actualizarProveedor_NoExistente_RetornaNotFound() throws Exception {
        ProveedorRequestDTO request = new ProveedorRequestDTO(
                "Proveedor Modificado", "12345678-9", "test@proveedor.com", "912345678", "Calle Test 123");

        when(proveedorService.actualizarProveedor(eq(99L), any(ProveedorRequestDTO.class)))
                .thenThrow(new ProveedorNoEncontradoException("Proveedor no encontrado con id: 99"));

        mockMvc.perform(put("/api/proveedores/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ── eliminarProveedor ──────────────────────────────────────────────────────

    @Test
    void eliminarProveedor_Existente_RetornaNoContent() throws Exception {
        doNothing().when(proveedorService).eliminarProveedor(1L);

        mockMvc.perform(delete("/api/proveedores/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarProveedor_NoExistente_RetornaNotFound() throws Exception {
        doThrow(new ProveedorNoEncontradoException("Proveedor no encontrado con id: 99")).when(proveedorService).eliminarProveedor(99L);

        mockMvc.perform(delete("/api/proveedores/99"))
                .andExpect(status().isNotFound());
    }
}
