package com.microservice.producto_service;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import com.microservice.producto_service.controller.ProductoController;
import com.microservice.producto_service.dto.ProductoRequestDTO;
import com.microservice.producto_service.dto.ProductoResponseDTO;
import com.microservice.producto_service.service.ProductoService;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Utilidad de Spring para convertir Objetos a JSON y viceversa

    @MockitoBean
    private ProductoService service;

    ProductoRequestDTO requestA = new ProductoRequestDTO("prod1","libro",100.0,1L);
    ProductoRequestDTO requestB = new ProductoRequestDTO("prod2","libreta",90.0,1L);
    ProductoResponseDTO responseA = new ProductoResponseDTO(2L,"prod1","libro",100.0,1L,true);
    ProductoResponseDTO responseB = new ProductoResponseDTO(3L,"prod2","libreta",90.0,1L,true);
    @Test
    void crear_shouldReturn201yProducto() throws Exception{
    
        when(service.crear(any(ProductoRequestDTO.class))).thenReturn(responseA);

        // Actuar y Afirmar
        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestA))) // Convertimos el DTO a JSON
                .andExpect(status().isCreated());
                
    }
    @Test
    void obtenerTodos_deberiaRetornar200yListaDeProductos() throws Exception {
        // Preparar
        List<ProductoResponseDTO> listaProductos = Arrays.asList(
            responseA,responseB
        );
        when(service.obtenerTodos()).thenReturn(listaProductos);

        // Actuar y Afirmar
        mockMvc.perform(get("/api/productos"))
               .andExpect(status().isOk()) // Verifica HTTP 200
               .andExpect(jsonPath("$", hasSize(2))); // Verifica que el JSON devuelva un arreglo de tamaño 2
    }

    @Test
    void obtener_deberiaRetornar200yProducto() throws Exception {
        // Preparar
        Long idProducto = 1L;
        ProductoResponseDTO responseDTO = new ProductoResponseDTO();
        when(service.obtenerPorId(idProducto)).thenReturn(responseDTO);

        // Actuar y Afirmar
        mockMvc.perform(get("/api/productos/{id}", idProducto))
               .andExpect(status().isOk());
    }

    @Test
    void actualizar_deberiaRetornar200yProductoActualizado() throws Exception {
        // Preparar
        Long idProducto = 2L;
        
        when(service.actualizar(eq(idProducto),any(ProductoRequestDTO.class))).thenReturn(responseB);

        // Actuar y Afirmar
        mockMvc.perform(put("/api/productos/{id}", idProducto)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(responseB)))
                .andExpect(status().isOk());
    }

    @Test
    void desactivar_deberiaRetornar200yMensaje() throws Exception {
        // Preparar
        Long idProducto = 1L;
        // doNothing() se usa para métodos void
        doNothing().when(service).desactivar(idProducto);

        // Actuar y Afirmar
        mockMvc.perform(put("/api/productos/{id}/desactivar", idProducto))
               .andExpect(status().isOk())
               .andExpect(content().string("Producto desactivado exitosamente"));
    }

    @Test
    void activar_deberiaRetornar200yMensaje() throws Exception {
        // Preparar
        Long idProducto = 1L;
        doNothing().when(service).activar(idProducto);

        // Actuar y Afirmar
        mockMvc.perform(put("/api/productos/{id}/activar", idProducto))
               .andExpect(status().isOk())
               .andExpect(content().string("Producto activado exitosamente"));
    }

    @Test
    void eliminar_deberiaRetornar204NoContent() throws Exception {
        // Preparar
        Long idProducto = 1L;
        doNothing().when(service).eliminar(idProducto);

        // Actuar y Afirmar
        mockMvc.perform(delete("/api/productos/{id}", idProducto))
               .andExpect(status().isNoContent()); // Verifica HTTP 204
    }
    
    

}