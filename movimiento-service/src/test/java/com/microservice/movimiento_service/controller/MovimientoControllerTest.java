package com.microservice.movimiento_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.movimiento_service.dto.MovimientoRequestDTO;
import com.microservice.movimiento_service.dto.MovimientoResponseDTO;
import com.microservice.movimiento_service.exception.ManejadorGlobal.MovimientoNoEncontradoException;
import com.microservice.movimiento_service.exception.ManejadorGlobal.ValidacionMovimientoException;
import com.microservice.movimiento_service.model.TipoMovimiento;
import com.microservice.movimiento_service.service.MovimientoService;

@WebMvcTest(MovimientoController.class)

@TestPropertySource(properties = {
                "spring.cloud.config.enabled=false",
                "eureka.client.enabled=false",
                "spring.cloud.discovery.enabled=false"
})
class MovimientoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private MovimientoService service;

        @Autowired
        private ObjectMapper objectMapper;

        private MovimientoResponseDTO responseEjemplo(Long id, TipoMovimiento tipo) {
                MovimientoResponseDTO dto = new MovimientoResponseDTO();
                dto.setId(id);
                dto.setProductoId(1L);
                dto.setBodegaOrigenId(
                                tipo == TipoMovimiento.SALIDA || tipo == TipoMovimiento.TRANSFERENCIA ? 1L : null);
                dto.setBodegaDestinoId(
                                tipo == TipoMovimiento.ENTRADA || tipo == TipoMovimiento.TRANSFERENCIA ? 2L : null);
                dto.setTipo(tipo);
                dto.setCantidad(50);
                dto.setMotivo("Motivo de prueba");
                dto.setFecha(LocalDateTime.of(2026, 6, 18, 10, 0));
                return dto;
        }

        private MovimientoRequestDTO requestEjemplo(TipoMovimiento tipo) {
                MovimientoRequestDTO dto = new MovimientoRequestDTO();
                dto.setProductoId(1L);
                dto.setTipo(tipo);
                dto.setCantidad(50);
                dto.setMotivo("Motivo de prueba");
                if (tipo == TipoMovimiento.ENTRADA || tipo == TipoMovimiento.TRANSFERENCIA) {
                        dto.setBodegaDestinoId(2L);
                }
                if (tipo == TipoMovimiento.SALIDA || tipo == TipoMovimiento.TRANSFERENCIA) {
                        dto.setBodegaOrigenId(1L);
                }
                return dto;
        }

        @Test
        void registrar_entradaValida_deberiaRetornarStatus201() throws Exception {
                MovimientoRequestDTO request = requestEjemplo(TipoMovimiento.ENTRADA);
                MovimientoResponseDTO response = responseEjemplo(1L, TipoMovimiento.ENTRADA);
                when(service.registrar(any(MovimientoRequestDTO.class))).thenReturn(response);

                mockMvc.perform(post("/api/movimientos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.tipo").value("ENTRADA"))
                                .andExpect(jsonPath("$.cantidad").value(50))
                                .andExpect(jsonPath("$.motivo").value("Motivo de prueba"));
        }

        @Test
        void registrar_salidaValida_deberiaRetornarStatus201() throws Exception {
                MovimientoRequestDTO request = requestEjemplo(TipoMovimiento.SALIDA);
                MovimientoResponseDTO response = responseEjemplo(2L, TipoMovimiento.SALIDA);
                when(service.registrar(any(MovimientoRequestDTO.class))).thenReturn(response);

                mockMvc.perform(post("/api/movimientos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.tipo").value("SALIDA"))
                                .andExpect(jsonPath("$.bodegaOrigenId").value(1));
        }

        @Test
        void registrar_transferenciaValida_deberiaRetornarStatus201() throws Exception {
                MovimientoRequestDTO request = requestEjemplo(TipoMovimiento.TRANSFERENCIA);
                MovimientoResponseDTO response = responseEjemplo(3L, TipoMovimiento.TRANSFERENCIA);
                when(service.registrar(any(MovimientoRequestDTO.class))).thenReturn(response);

                mockMvc.perform(post("/api/movimientos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.tipo").value("TRANSFERENCIA"))
                                .andExpect(jsonPath("$.bodegaOrigenId").value(1))
                                .andExpect(jsonPath("$.bodegaDestinoId").value(2));
        }

        @Test
        void registrar_sinProductoId_deberiaRetornarStatus400() throws Exception {
                MovimientoRequestDTO request = requestEjemplo(TipoMovimiento.ENTRADA);
                request.setProductoId(null);

                mockMvc.perform(post("/api/movimientos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void registrar_sinTipo_deberiaRetornarStatus400() throws Exception {
                MovimientoRequestDTO request = requestEjemplo(TipoMovimiento.ENTRADA);
                request.setTipo(null);

                mockMvc.perform(post("/api/movimientos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void registrar_cantidadNegativa_deberiaRetornarStatus400() throws Exception {
                MovimientoRequestDTO request = requestEjemplo(TipoMovimiento.ENTRADA);
                request.setCantidad(-5);

                mockMvc.perform(post("/api/movimientos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void registrar_motivoVacio_deberiaRetornarStatus400() throws Exception {
                MovimientoRequestDTO request = requestEjemplo(TipoMovimiento.ENTRADA);
                request.setMotivo("");

                mockMvc.perform(post("/api/movimientos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void registrar_cuerpoVacio_deberiaRetornarStatus400() throws Exception {
                mockMvc.perform(post("/api/movimientos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Error de validación"));
        }

        @Test
        void registrar_validacionNegocioFalla_deberiaRetornarStatus400() throws Exception {
                MovimientoRequestDTO request = requestEjemplo(TipoMovimiento.ENTRADA);
                when(service.registrar(any())).thenThrow(
                                new ValidacionMovimientoException("El tipo ENTRADA requiere bodegaDestinoId."));

                mockMvc.perform(post("/api/movimientos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Error de validación"));
        }

        // ---- GET /api/movimientos ----

        @Test
        void obtenerTodos_deberiaRetornarListaConStatus200() throws Exception {
                MovimientoResponseDTO r1 = responseEjemplo(1L, TipoMovimiento.ENTRADA);
                MovimientoResponseDTO r2 = responseEjemplo(2L, TipoMovimiento.SALIDA);
                when(service.obtenerTodos()).thenReturn(List.of(r1, r2));

                mockMvc.perform(get("/api/movimientos"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].tipo").value("ENTRADA"))
                                .andExpect(jsonPath("$[1].tipo").value("SALIDA"));
        }

        @Test
        void obtenerTodos_sinMovimientos_deberiaRetornarListaVaciaConStatus200() throws Exception {
                when(service.obtenerTodos()).thenReturn(List.of());

                mockMvc.perform(get("/api/movimientos"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(0));
        }

        // ---- GET /api/movimientos/{id} ----

        @Test
        void obtenerPorId_existente_deberiaRetornarMovimientoConStatus200() throws Exception {
                when(service.obtenerPorId(1L)).thenReturn(responseEjemplo(1L, TipoMovimiento.ENTRADA));

                mockMvc.perform(get("/api/movimientos/1"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.tipo").value("ENTRADA"))
                                .andExpect(jsonPath("$.cantidad").value(50));
        }

        @Test
        void obtenerPorId_inexistente_deberiaRetornarStatus404() throws Exception {
                when(service.obtenerPorId(99L)).thenThrow(
                                new MovimientoNoEncontradoException("Movimiento no encontrado con id: 99"));

                mockMvc.perform(get("/api/movimientos/99"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.error").value("Recurso no encontrado"))
                                .andExpect(jsonPath("$.mensaje").value("Movimiento no encontrado con id: 99"));
        }

        // ---- GET /api/movimientos/producto/{productoId} ----

        @Test
        void obtenerPorProducto_deberiaRetornarListaConStatus200() throws Exception {
                when(service.obtenerPorProducto(1L)).thenReturn(
                                List.of(responseEjemplo(1L, TipoMovimiento.ENTRADA)));

                mockMvc.perform(get("/api/movimientos/producto/1"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].productoId").value(1));
        }

        @Test
        void obtenerPorProducto_sinMovimientos_deberiaRetornarListaVacia() throws Exception {
                when(service.obtenerPorProducto(99L)).thenReturn(List.of());

                mockMvc.perform(get("/api/movimientos/producto/99"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(0));
        }

        // ---- GET /api/movimientos/bodega/{bodegaId} ----

        @Test
        void obtenerPorBodega_deberiaRetornarListaConStatus200() throws Exception {
                when(service.obtenerPorBodega(1L)).thenReturn(
                                List.of(responseEjemplo(1L, TipoMovimiento.TRANSFERENCIA)));

                mockMvc.perform(get("/api/movimientos/bodega/1"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        void obtenerPorBodega_sinMovimientos_deberiaRetornarListaVacia() throws Exception {
                when(service.obtenerPorBodega(99L)).thenReturn(List.of());

                mockMvc.perform(get("/api/movimientos/bodega/99"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(0));
        }

        // ---- GET /api/movimientos/tipo/{tipo} ----

        @Test
        void obtenerPorTipo_ENTRADA_deberiaRetornarMovimientosConStatus200() throws Exception {
                when(service.obtenerPorTipo(TipoMovimiento.ENTRADA)).thenReturn(
                                List.of(responseEjemplo(1L, TipoMovimiento.ENTRADA)));

                mockMvc.perform(get("/api/movimientos/tipo/ENTRADA"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].tipo").value("ENTRADA"));
        }

        @Test
        void obtenerPorTipo_SALIDA_deberiaRetornarMovimientosConStatus200() throws Exception {
                when(service.obtenerPorTipo(TipoMovimiento.SALIDA)).thenReturn(
                                List.of(responseEjemplo(2L, TipoMovimiento.SALIDA)));

                mockMvc.perform(get("/api/movimientos/tipo/SALIDA"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].tipo").value("SALIDA"));
        }

        @Test
        void obtenerPorTipo_TRANSFERENCIA_deberiaRetornarMovimientosConStatus200() throws Exception {
                when(service.obtenerPorTipo(TipoMovimiento.TRANSFERENCIA)).thenReturn(
                                List.of(responseEjemplo(3L, TipoMovimiento.TRANSFERENCIA)));

                mockMvc.perform(get("/api/movimientos/tipo/TRANSFERENCIA"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].tipo").value("TRANSFERENCIA"));
        }

        @Test
        void obtenerPorTipo_tipoInvalido_deberiaRetornarStatus400() throws Exception {
                mockMvc.perform(get("/api/movimientos/tipo/TIPO_INEXISTENTE"))
                                .andExpect(status().isBadRequest());
        }
}
