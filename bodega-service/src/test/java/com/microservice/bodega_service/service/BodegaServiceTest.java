package com.microservice.bodega_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.microservice.bodega_service.dto.BodegaRequest;
import com.microservice.bodega_service.dto.BodegaResponse;
import com.microservice.bodega_service.exception.ManejadorGlobal.BodegaNotFoundException;
import com.microservice.bodega_service.model.Bodega;
import com.microservice.bodega_service.repository.BodegaRepository;

@ExtendWith(MockitoExtension.class)
class BodegaServiceTest {

    @Mock
    private BodegaRepository repository;

    @InjectMocks
    private BodegaService service;

    private Bodega bodegaDePrueba(Long id, String nombre, String ubicacion, Integer capacidad) {
        Bodega b = new Bodega();
        b.setId(id);
        b.setNombre(nombre);
        b.setUbicacion(ubicacion);
        b.setCapacidadMaximaItems(capacidad);
        return b;
    }

    // ---- listar() ----

    @Test
    void listar_sinBodegas_deberiaRetornarListaVacia() {
        when(repository.findAll()).thenReturn(List.of());

        List<BodegaResponse> resultado = service.listar();

        assertTrue(resultado.isEmpty());
        verify(repository).findAll();
    }

    @Test
    void listar_conBodegas_deberiaRetornarListaConElementos() {
        Bodega b1 = bodegaDePrueba(1L, "Bodega A", "Calle 1", 100);
        Bodega b2 = bodegaDePrueba(2L, "Bodega B", "Calle 2", 200);
        when(repository.findAll()).thenReturn(List.of(b1, b2));

        List<BodegaResponse> resultado = service.listar();

        assertEquals(2, resultado.size());
        assertEquals("Bodega A", resultado.get(0).getNombre());
        assertEquals("Calle 1", resultado.get(0).getUbicacion());
        assertEquals(100, resultado.get(0).getCapacidadMaximaItems());
        assertEquals("Bodega B", resultado.get(1).getNombre());
        verify(repository).findAll();
    }

    // ---- guardar() ----

    @Test
    void guardar_datosValidos_deberiaGuardar() {
        BodegaRequest request = new BodegaRequest("Bodega Central", "Av. Industrial 100", 500);
        Bodega guardada = bodegaDePrueba(1L, "Bodega Central", "Av. Industrial 100", 500);
        when(repository.save(any(Bodega.class))).thenReturn(guardada);

        BodegaResponse resultado = service.guardar(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Bodega Central", resultado.getNombre());
        assertEquals("Av. Industrial 100", resultado.getUbicacion());
        assertEquals(500, resultado.getCapacidadMaximaItems());

        ArgumentCaptor<Bodega> captor = ArgumentCaptor.forClass(Bodega.class);
        verify(repository).save(captor.capture());
        assertEquals("Bodega Central", captor.getValue().getNombre());
        assertEquals("Av. Industrial 100", captor.getValue().getUbicacion());
        assertEquals(500, captor.getValue().getCapacidadMaximaItems());
    }

    @Test
    void guardar_datosValidos_deberiaMapearCorrectamenteTodosLosCampos() {
        BodegaRequest request = new BodegaRequest("Bodega Norte", "Zona Norte 50", 1000);
        Bodega guardada = bodegaDePrueba(5L, "Bodega Norte", "Zona Norte 50", 1000);
        when(repository.save(any(Bodega.class))).thenReturn(guardada);

        BodegaResponse resultado = service.guardar(request);

        assertEquals(5L, resultado.getId());
        assertEquals("Bodega Norte", resultado.getNombre());
        assertEquals("Zona Norte 50", resultado.getUbicacion());
        assertEquals(1000, resultado.getCapacidadMaximaItems());
    }

    // ---- buscarPorId() ----

    @Test
    void buscarPorId_existente_deberiaRetornarBodega() {
        Bodega b = bodegaDePrueba(1L, "Bodega Norte", "Zona Norte 50", 300);
        when(repository.findById(1L)).thenReturn(Optional.of(b));

        BodegaResponse resultado = service.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Bodega Norte", resultado.getNombre());
        assertEquals("Zona Norte 50", resultado.getUbicacion());
        assertEquals(300, resultado.getCapacidadMaximaItems());
        verify(repository).findById(1L);
    }

    @Test
    void buscarPorId_inexistente_deberiaLanzarExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        BodegaNotFoundException ex = assertThrows(
                BodegaNotFoundException.class,
                () -> service.buscarPorId(99L)
        );

        assertTrue(ex.getMessage().contains("99"));
        verify(repository).findById(99L);
    }

    @Test
    void buscarPorId_inexistente_mensajeDeExcepcionDebeContenerElId() {
        when(repository.findById(42L)).thenReturn(Optional.empty());

        BodegaNotFoundException ex = assertThrows(
                BodegaNotFoundException.class,
                () -> service.buscarPorId(42L)
        );

        assertTrue(ex.getMessage().contains("42"));
    }

    // ---- eliminar() ----

    @Test
    void eliminar_existente_deberiaEliminar() {
        Bodega b = bodegaDePrueba(1L, "Bodega Sur", "Zona Sur 10", 150);
        when(repository.findById(1L)).thenReturn(Optional.of(b));

        service.eliminar(1L);

        verify(repository).findById(1L);
        verify(repository).delete(b);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void eliminar_inexistente_deberiaLanzarExcepcionYNoEliminar() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BodegaNotFoundException.class, () -> service.eliminar(99L));

        verify(repository).findById(99L);
        verify(repository, never()).delete(any());
    }

    @Test
    void eliminar_inexistente_mensajeDeExcepcionDebeContenerElId() {
        when(repository.findById(55L)).thenReturn(Optional.empty());

        BodegaNotFoundException ex = assertThrows(
                BodegaNotFoundException.class,
                () -> service.eliminar(55L)
        );

        assertTrue(ex.getMessage().contains("55"));
    }
}
