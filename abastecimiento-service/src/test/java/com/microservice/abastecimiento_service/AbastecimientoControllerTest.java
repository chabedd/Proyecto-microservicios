package com.microservice.abastecimiento_service;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.abastecimiento_service.controller.AbastecimientoController;
import com.microservice.abastecimiento_service.dto.OrdenCompraRequestDTO;
import com.microservice.abastecimiento_service.dto.OrdenCompraResponseDTO;
import com.microservice.abastecimiento_service.service.AbastecimientoService;

@WebMvcTest(AbastecimientoController.class)
class AbastecimientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AbastecimientoService service;

    // Instanciamos los objetos vacíos (o con constructores si los tuvieras) para usarlos en los tests
    OrdenCompraRequestDTO requestDTO = new OrdenCompraRequestDTO();
    OrdenCompraResponseDTO responseA = new OrdenCompraResponseDTO();
    OrdenCompraResponseDTO responseB = new OrdenCompraResponseDTO();

    @Test
    void crear_deberiaRetornar201yOrdenDeCompra() throws Exception {
        when(service.crearOrden(any(OrdenCompraRequestDTO.class))).thenReturn(responseA);

        mockMvc.perform(post("/api/abastecimiento")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void obtenerTodas_deberiaRetornar200yListaDeOrdenes() throws Exception {
        List<OrdenCompraResponseDTO> listaOrdenes = Arrays.asList(responseA, responseB);
        when(service.obtenerTodas()).thenReturn(listaOrdenes);

        mockMvc.perform(get("/api/abastecimiento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void obtener_deberiaRetornar200yOrdenDeCompra() throws Exception {
        Long idOrden = 1L;
        when(service.obtenerPorId(idOrden)).thenReturn(responseA);

        mockMvc.perform(get("/api/abastecimiento/{id}", idOrden))
                .andExpect(status().isOk());
    }

    @Test
    void cambiarEstado_conBodegaId_deberiaRetornar200yOrdenActualizada() throws Exception {
        Long idOrden = 1L;
        String nuevoEstado = "RECIBIDA";
        Long bodegaId = 5L;

        when(service.cambiarEstado(idOrden, nuevoEstado, bodegaId)).thenReturn(responseA);

        // Pasamos los RequestParam usando el método .param()
        mockMvc.perform(put("/api/abastecimiento/{id}/estado", idOrden)
                .param("estado", nuevoEstado)
                .param("bodegaId", bodegaId.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void cambiarEstado_sinBodegaId_deberiaRetornar200yOrdenActualizada() throws Exception {
        Long idOrden = 1L;
        String nuevoEstado = "EN_PROCESO";
        
        // Aquí simulamos que el bodegaId llega como null (porque es required = false)
        when(service.cambiarEstado(idOrden, nuevoEstado, null)).thenReturn(responseA);

        // Solo enviamos el parámetro "estado"
        mockMvc.perform(put("/api/abastecimiento/{id}/estado", idOrden)
                .param("estado", nuevoEstado))
                .andExpect(status().isOk());
    }

    @Test
    void cancelarOrden_deberiaRetornar204NoContent() throws Exception {
        Long idOrden = 1L;
        doNothing().when(service).cancelarOrden(idOrden);

        mockMvc.perform(delete("/api/abastecimiento/{id}", idOrden))
                .andExpect(status().isNoContent());
    }

    @Test
    void tieneOrdenesActivas_deberiaRetornar200yBooleano() throws Exception {
        Long proveedorId = 10L;
        when(service.tieneOrdenesActivasPorProveedor(proveedorId)).thenReturn(true);

        mockMvc.perform(get("/api/abastecimiento/proveedor/{proveedorId}/tiene-ordenes-activas", proveedorId))
                .andExpect(status().isOk())
                .andExpect(content().string("true")); // Spring convierte el boolean a un string "true" o "false" en el JSON
    }
}