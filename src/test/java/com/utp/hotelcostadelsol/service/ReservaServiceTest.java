package com.utp.hotelcostadelsol.service;

import com.utp.hotelcostadelsol.dao.IAuditoriaDAO;
import com.utp.hotelcostadelsol.dao.IHabitacionDAO;
import com.utp.hotelcostadelsol.dao.IReservaDAO;
import com.utp.hotelcostadelsol.dao.ITipoHabitacionDAO;
import com.utp.hotelcostadelsol.model.dto.CotizacionDTO;
import com.utp.hotelcostadelsol.model.dto.ReservaDTO;
import com.utp.hotelcostadelsol.model.dto.TipoHabitacionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias TDD para ReservaService validando cotizaciones, Google Guava Preconditions
 * y generacion de codigos con Apache Commons RandomStringUtils.
 */
@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private IReservaDAO reservaDAO;

    @Mock
    private ITipoHabitacionDAO tipoHabitacionDAO;

    @Mock
    private IHabitacionDAO habitacionDAO;

    @Mock
    private IAuditoriaDAO auditoriaDAO;

    private ReservaService reservaService;

    @BeforeEach
    void setUp() {
        reservaService = new ReservaService(reservaDAO, tipoHabitacionDAO, habitacionDAO, auditoriaDAO);
    }

    @Test
    @DisplayName("Debe calcular cotizacion correctamente por cantidad de noches y servicios extra")
    void shouldCalculateQuotationCorrectly() {
        TipoHabitacionDTO deluxe = new TipoHabitacionDTO(2, "matrimonial", "Habitacion Matrimonial Deluxe",
                "King Size", new BigDecimal("180.00"), 2);
        when(tipoHabitacionDAO.findById(2)).thenReturn(Optional.of(deluxe));

        LocalDate checkin = LocalDate.of(2026, 11, 10);
        LocalDate checkout = LocalDate.of(2026, 11, 13); // 3 noches

        CotizacionDTO cot = reservaService.calcularCotizacion(2, checkin, checkout, new BigDecimal("50.00"));

        assertEquals(3L, cot.getNoches());
        assertEquals(new BigDecimal("540.00"), cot.getSubtotalAlojamiento());
        assertEquals(new BigDecimal("590.00"), cot.getMontoTotal());
    }

    @Test
    @DisplayName("Debe rechazar fechas invalidas donde checkout no es posterior a checkin (Guava Preconditions)")
    void shouldRejectInvalidDateRange() {
        LocalDate checkin = LocalDate.of(2026, 11, 15);
        LocalDate checkout = LocalDate.of(2026, 11, 12);

        assertThrows(IllegalArgumentException.class,
                () -> reservaService.calcularCotizacion(1, checkin, checkout, BigDecimal.ZERO));
    }

    @Test
    @DisplayName("Debe crear reserva generando codigo WYN-2026-XXXXXX con Apache Commons y registrar auditoria")
    void shouldCreateReservationAndGenerateCode() {
        TipoHabitacionDTO suite = new TipoHabitacionDTO(4, "suite_jr", "Suite Junior", "VIP", new BigDecimal("320.00"), 2);
        when(tipoHabitacionDAO.findById(4)).thenReturn(Optional.of(suite));
        when(reservaDAO.save(any(ReservaDTO.class))).thenAnswer(inv -> {
            ReservaDTO arg = inv.getArgument(0);
            arg.setIdReserva(10);
            return arg;
        });

        ReservaDTO nueva = new ReservaDTO();
        nueva.setIdCliente(3);
        nueva.setNombreCliente("Joel Cori");
        nueva.setIdTipoHabitacion(4);
        nueva.setNumeroHuespedes(2);
        nueva.setFechaCheckin(LocalDate.now().plusDays(1));
        nueva.setFechaCheckout(LocalDate.now().plusDays(3));

        ReservaDTO creada = reservaService.crearReserva(nueva);

        assertNotNull(creada.getCodigoReserva());
        assertTrue(creada.getCodigoReserva().startsWith("WYN-2026-"));
        assertEquals(new BigDecimal("640.00"), creada.getMontoTotal());
        verify(auditoriaDAO, times(1)).registrarEvento(any());
    }
}
