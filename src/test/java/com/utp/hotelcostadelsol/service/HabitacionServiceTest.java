package com.utp.hotelcostadelsol.service;

import com.utp.hotelcostadelsol.dao.IHabitacionDAO;
import com.utp.hotelcostadelsol.dao.TipoHabitacionDAO;
import com.utp.hotelcostadelsol.model.dto.HabitacionDTO;
import com.utp.hotelcostadelsol.model.dto.TipoHabitacionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias TDD para HabitacionService validando SOLID (DIP) y cache de Google Guava.
 */
@ExtendWith(MockitoExtension.class)
class HabitacionServiceTest {

    @Mock
    private IHabitacionDAO habitacionDAO;

    @Mock
    private TipoHabitacionDAO tipoHabitacionDAO;

    private HabitacionService habitacionService;

    @BeforeEach
    void setUp() {
        habitacionService = new HabitacionService(habitacionDAO, tipoHabitacionDAO);
    }

    @Test
    @DisplayName("Debe almacenar en cache de Google Guava el catalogo de tipos de habitacion")
    void shouldCacheRoomTypesWithGuava() {
        List<TipoHabitacionDTO> tipos = List.of(
                new TipoHabitacionDTO(1, "simple", "Simple", "Desc", new BigDecimal("120.00"), 1)
        );
        when(tipoHabitacionDAO.findAll()).thenReturn(tipos);

        List<TipoHabitacionDTO> firstCall = habitacionService.listarTipos();
        List<TipoHabitacionDTO> secondCall = habitacionService.listarTipos();

        assertEquals(1, firstCall.size());
        assertEquals(1, secondCall.size());
        // Verifica que solo consulto al DAO una vez gracias a CacheBuilder de Guava
        verify(tipoHabitacionDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe filtrar solo las habitaciones con estado DISPONIBLE")
    void shouldFilterAvailableRooms() {
        when(habitacionDAO.findAll()).thenReturn(List.of(
                new HabitacionDTO(1, 1, "101", 1, "Simple", "DISPONIBLE", new BigDecimal("120.00"), ""),
                new HabitacionDTO(2, 1, "102", 1, "Simple", "OCUPADA", new BigDecimal("120.00"), "")
        ));

        List<HabitacionDTO> disponibles = habitacionService.listarDisponibles();

        assertEquals(1, disponibles.size());
        assertEquals("101", disponibles.get(0).getNumero());
    }
}
