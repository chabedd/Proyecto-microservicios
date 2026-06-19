package com.microservice.bodega_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.bodega_service.dto.BodegaRequest;
import com.microservice.bodega_service.dto.BodegaResponse;
import com.microservice.bodega_service.exception.ManejadorGlobal.BodegaNotFoundException;
import com.microservice.bodega_service.service.BodegaService;

@WebMvcTest(BodegaController.class)
@TestPropertySource(properties = {
                "spring.cloud.config.enabled=false",
                "eureka.client.enabled=false",
                "spring.cloud.discovery.enabled=false"
})
class BodegaControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private BodegaService service;

        @Autowired
        private ObjectMapper objectMapper;

        private BodegaResponse responseEjemplo() {
                BodegaResponse r = new BodegaResponse();
                r.setId(1L);
                r.setNombre("Bodega Central");
                r.setUbicacion("Av. Industrial 100");
                r.setCapacidadMaximaItems(500);
                return r;
        }

        // ---- GET /api/bodegas ----

        @Test
        void listar_deberiaRetornarListaConStatus200() throws Exception {
                BodegaResponse r1 = responseEjemplo();
                BodegaResponse r2 = new BodegaResponse(2L, "Bodega Sur", "Zona Sur 20", 300);
                when(service.listar()).thenReturn(List.of(r1, r2));

                mockMvc.perform(get("/api/bodegas"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].nombre").value("Bodega Central"))
                                .andExpect(jsonPath("$[1].nombre").value("Bodega Sur"));
        }

        @Test
        void listar_sinBodegas_deberiaRetornarListaVaciaConStatus200() throws Exception {
                when(service.listar()).thenReturn(List.of());

                mockMvc.perform(get("/api/bodegas"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(0));
        }

        // ---- GET /api/bodegas/{id} ----

        @Test
        void buscarPorId_existente_deberiaRetornarBodegaConStatus200() throws Exception {
                when(service.buscarPorId(1L)).thenReturn(responseEjemplo());

                mockMvc.perform(get("/api/bodegas/1"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.nombre").value("Bodega Central"))
                                .andExpect(jsonPath("$.ubicacion").value("Av. Industrial 100"))
                                .andExpect(jsonPath("$.capacidadMaximaItems").value(500));
        }

        @Test
        void buscarPorId_inexistente_deberiaRetornarStatus404() throws Exception {
                when(service.buscarPorId(99L))
                                .thenThrow(new BodegaNotFoundException("Bodega no encontrada con id: 99"));

                mockMvc.perform(get("/api/bodegas/99"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.error").value("Bodega no encontrada"))
                                .andExpect(jsonPath("$.mensaje").value("Bodega no encontrada con id: 99"));
        }

        // ---- POST /api/bodegas ----

        @Test
        void guardar_datosValidos_deberiaRetornarBodegaConStatus201() throws Exception {
                BodegaRequest request = new BodegaRequest("Bodega Central", "Av. Industrial 100", 500);
                when(service.guardar(any(BodegaRequest.class))).thenReturn(responseEjemplo());

                mockMvc.perform(post("/api/bodegas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.nombre").value("Bodega Central"));
        }

        @Test
        void guardar_nombreVacio_deberiaRetornarStatus400() throws Exception {
                BodegaRequest request = new BodegaRequest("", "Av. Industrial 100", 500);

                mockMvc.perform(post("/api/bodegas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void guardar_ubicacionVacia_deberiaRetornarStatus400() throws Exception {
                BodegaRequest request = new BodegaRequest("Bodega X", "", 500);

                mockMvc.perform(post("/api/bodegas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void guardar_capacidadNegativa_deberiaRetornarStatus400() throws Exception {
                BodegaRequest request = new BodegaRequest("Bodega X", "Calle 1", -10);

                mockMvc.perform(post("/api/bodegas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void guardar_capacidadNula_deberiaRetornarStatus400() throws Exception {
                BodegaRequest request = new BodegaRequest("Bodega X", "Calle 1", null);

                mockMvc.perform(post("/api/bodegas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void guardar_cuerpoVacio_deberiaRetornarStatus400() throws Exception {
                mockMvc.perform(post("/api/bodegas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Error de validación"));
        }

        // ---- DELETE /api/bodegas/{id} ----

        @Test
        void eliminar_existente_deberiaRetornarStatus204() throws Exception {
                doNothing().when(service).eliminar(1L);

                mockMvc.perform(delete("/api/bodegas/1"))
                                .andExpect(status().isNoContent());

                verify(service).eliminar(1L);
        }

        @Test
        void eliminar_inexistente_deberiaRetornarStatus404() throws Exception {
                doThrow(new BodegaNotFoundException("Bodega no encontrada con id: 99"))
                                .when(service).eliminar(99L);

                mockMvc.perform(delete("/api/bodegas/99"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.error").value("Bodega no encontrada"));
        }
}
