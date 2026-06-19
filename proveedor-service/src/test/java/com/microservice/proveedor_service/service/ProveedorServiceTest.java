package com.microservice.proveedor_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.microservice.proveedor_service.dto.ProveedorRequestDTO;
import com.microservice.proveedor_service.dto.ProveedorResponseDTO;
import com.microservice.proveedor_service.exception.ManejadorGlobal.ProveedorNoEncontradoException;
import com.microservice.proveedor_service.exception.ManejadorGlobal.RutDuplicadoException;
import com.microservice.proveedor_service.mapper.ProveedorMapper;
import com.microservice.proveedor_service.model.Proveedor;
import com.microservice.proveedor_service.repository.ProveedorRepository;

@ExtendWith(MockitoExtension.class)
class ProveedorServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @Mock
    private ProveedorMapper proveedorMapper;

    @InjectMocks
    private ProveedorService proveedorService;

    private Proveedor proveedor;
    private ProveedorRequestDTO requestDTO;
    private ProveedorResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        proveedor = new Proveedor();
        proveedor.setId(1L);
        proveedor.setNombre("Proveedor Test");
        proveedor.setRut("12345678-9");
        proveedor.setEmail("test@proveedor.com");
        proveedor.setTelefono("912345678");
        proveedor.setDireccion("Calle Test 123");
        proveedor.setFechaRegistro(LocalDateTime.now());

        requestDTO = new ProveedorRequestDTO(
                "Proveedor Test", "12345678-9", "test@proveedor.com", "912345678", "Calle Test 123");

        responseDTO = new ProveedorResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setNombre("Proveedor Test");
        responseDTO.setRut("12345678-9");
    }

    // ── crearProveedor ─────────────────────────────────────────────────────────

    @Test
    void crearProveedor_DatosValidos_RetornaResponseDTO() {
        when(proveedorMapper.toEntity(requestDTO)).thenReturn(proveedor);
        when(proveedorRepository.save(proveedor)).thenReturn(proveedor);
        when(proveedorMapper.toDTO(proveedor)).thenReturn(responseDTO);

        ProveedorResponseDTO result = proveedorService.crearProveedor(requestDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(proveedorRepository).save(proveedor);
    }

    @Test
    void crearProveedor_RutDuplicado_LanzaRutDuplicadoException() {
        when(proveedorMapper.toEntity(requestDTO)).thenReturn(proveedor);
        when(proveedorRepository.save(proveedor)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(RutDuplicadoException.class, () -> proveedorService.crearProveedor(requestDTO));
    }

    // ── obtenerProveedorPorId ──────────────────────────────────────────────────

    @Test
    void obtenerProveedorPorId_Existente_RetornaResponseDTO() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));
        when(proveedorMapper.toDTO(proveedor)).thenReturn(responseDTO);

        ProveedorResponseDTO result = proveedorService.obtenerProveedorPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void obtenerProveedorPorId_NoExistente_LanzaProveedorNoEncontradoException() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProveedorNoEncontradoException.class,
                () -> proveedorService.obtenerProveedorPorId(99L));
    }

    // ── obtenerTodosProveedores ────────────────────────────────────────────────

    @Test
    void obtenerTodosProveedores_HayProveedores_RetornaLista() {
        when(proveedorRepository.findAll()).thenReturn(List.of(proveedor));
        when(proveedorMapper.toDTO(proveedor)).thenReturn(responseDTO);

        List<ProveedorResponseDTO> result = proveedorService.obtenerTodosProveedores();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void obtenerTodosProveedores_SinRegistros_RetornaListaVacia() {
        when(proveedorRepository.findAll()).thenReturn(List.of());

        List<ProveedorResponseDTO> result = proveedorService.obtenerTodosProveedores();

        assertTrue(result.isEmpty());
    }

    // ── actualizarProveedor ────────────────────────────────────────────────────

    @Test
    void actualizarProveedor_Existente_RetornaActualizado() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));
        when(proveedorRepository.save(proveedor)).thenReturn(proveedor);
        when(proveedorMapper.toDTO(proveedor)).thenReturn(responseDTO);

        ProveedorResponseDTO result = proveedorService.actualizarProveedor(1L, requestDTO);

        assertNotNull(result);
        verify(proveedorRepository).save(proveedor);
    }

    @Test
    void actualizarProveedor_NoExistente_LanzaProveedorNoEncontradoException() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProveedorNoEncontradoException.class,
                () -> proveedorService.actualizarProveedor(99L, requestDTO));
    }

    @Test
    void actualizarProveedor_RutDuplicado_LanzaRutDuplicadoException() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));
        when(proveedorRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        assertThrows(RutDuplicadoException.class,
                () -> proveedorService.actualizarProveedor(1L, requestDTO));
    }

    // ── eliminarProveedor ──────────────────────────────────────────────────────

    @Test
    void eliminarProveedor_Existente_EliminaExitosamente() {
        when(proveedorRepository.existsById(1L)).thenReturn(true);

        proveedorService.eliminarProveedor(1L);

        verify(proveedorRepository).deleteById(1L);
    }

    @Test
    void eliminarProveedor_NoExistente_LanzaProveedorNoEncontradoException() {
        when(proveedorRepository.existsById(99L)).thenReturn(false);

        assertThrows(ProveedorNoEncontradoException.class,
                () -> proveedorService.eliminarProveedor(99L));
        verify(proveedorRepository, never()).deleteById(any());
    }

    // ── obtenerProveedorPorRut ─────────────────────────────────────────────────

    @Test
    void obtenerProveedorPorRut_Existente_RetornaOptionalConDTO() {
        when(proveedorRepository.findByRut("12345678-9")).thenReturn(Optional.of(proveedor));
        when(proveedorMapper.toDTO(proveedor)).thenReturn(responseDTO);

        Optional<ProveedorResponseDTO> result = proveedorService.obtenerProveedorPorRut("12345678-9");

        assertTrue(result.isPresent());
    }

    @Test
    void obtenerProveedorPorRut_NoExistente_RetornaOptionalVacio() {
        when(proveedorRepository.findByRut("00000000-0")).thenReturn(Optional.empty());

        Optional<ProveedorResponseDTO> result = proveedorService.obtenerProveedorPorRut("00000000-0");

        assertTrue(result.isEmpty());
    }
}
