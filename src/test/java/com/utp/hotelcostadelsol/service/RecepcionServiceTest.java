package com.utp.hotelcostadelsol.service;

import com.utp.hotelcostadelsol.dao.AuditoriaDAO;
import com.utp.hotelcostadelsol.dao.EstanciaDAO;
import com.utp.hotelcostadelsol.dao.IHabitacionDAO;
import com.utp.hotelcostadelsol.dao.IReservaDAO;
import com.utp.hotelcostadelsol.dao.PagoDAO;
import com.utp.hotelcostadelsol.model.dto.EstanciaDTO;
import com.utp.hotelcostadelsol.model.dto.HabitacionDTO;
import com.utp.hotelcostadelsol.model.dto.PagoDTO;
import com.utp.hotelcostadelsol.model.dto.ReservaDTO;
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
 * Pruebas unitarias TDD para RecepcionService (Check-in, Check-out y Verificacion de Pagos).
 */
@ExtendWith(MockitoExtension.class)
class RecepcionServiceTest {

    @Mock
    private IReservaDAO reservaDAO;

    @Mock
    private IHabitacionDAO habitacionDAO;

    @Mock
    private EstanciaDAO estanciaDAO;

    @Mock
    private PagoDAO pagoDAO;

    @Mock
    private AuditoriaDAO auditoriaDAO;

    private RecepcionService recepcionService;

    @BeforeEach
    void setUp() {
        recepcionService = new RecepcionService(reservaDAO, habitacionDAO, estanciaDAO, pagoDAO, auditoriaDAO);
    }

    @Test
    @DisplayName("Debe verificar pago y cambiar reserva de PENDIENTE a CONFIRMADA")
    void shouldVerifyPaymentAndConfirmReservation() {
        ReservaDTO reserva = new ReservaDTO(4, "WYN-2026-X501JK", 3, "Joel Cori", 5, "Suite",
                null, "Sin asignar", LocalDate.now(), LocalDate.now().plusDays(2),
                2, new BigDecimal("1100.00"), BigDecimal.ZERO, new BigDecimal("1100.00"),
                "PENDIENTE", "WEB", "");
        when(reservaDAO.findById(4)).thenReturn(Optional.of(reserva));
        when(pagoDAO.save(any(PagoDTO.class))).thenAnswer(inv -> inv.getArgument(0));

        PagoDTO pago = recepcionService.verificarPago(4, new BigDecimal("1100.00"), "TARJETA_CREDITO", "OP-123", null, 2);

        assertEquals("VERIFICADO", pago.getEstadoPago());
        verify(reservaDAO).updateEstado(4, "CONFIRMADA");
        verify(auditoriaDAO).registrarEvento(any());
    }

    @Test
    @DisplayName("Debe registrar Check-in ocupando la habitacion y entregando llave RFID")
    void shouldRegisterCheckinAndOccupyRoom() {
        ReservaDTO reserva = new ReservaDTO(3, "WYN-2026-P201GH", 3, "Joel Cori", 2, "Matrimonial",
                3, "201", LocalDate.now(), LocalDate.now().plusDays(2),
                2, new BigDecimal("360.00"), BigDecimal.ZERO, new BigDecimal("360.00"),
                "CONFIRMADA", "WEB", "");
        HabitacionDTO hab = new HabitacionDTO(3, 2, "201", 2, "Matrimonial", "DISPONIBLE", new BigDecimal("180.00"), "");

        when(reservaDAO.findById(3)).thenReturn(Optional.of(reserva));
        when(habitacionDAO.findById(3)).thenReturn(Optional.of(hab));
        when(estanciaDAO.save(any(EstanciaDTO.class))).thenAnswer(inv -> inv.getArgument(0));

        EstanciaDTO estancia = recepcionService.registrarCheckin(3, 3, "key-rfid-201", 2);

        assertEquals("KEY-RFID-201", estancia.getLlaveAsignada());
        verify(habitacionDAO).updateEstado(3, "OCUPADA");
        verify(reservaDAO).updateEstado(3, "CHECK_IN");
    }

    @Test
    @DisplayName("Debe registrar Check-out liberando la habitacion a DISPONIBLE y finalizando la reserva")
    void shouldRegisterCheckoutAndReleaseRoom() {
        ReservaDTO reserva = new ReservaDTO(1, "WYN-2026-A102BC", 3, "Joel Cori", 1, "Simple",
                2, "102", LocalDate.now().minusDays(1), LocalDate.now(),
                1, new BigDecimal("120.00"), BigDecimal.ZERO, new BigDecimal("120.00"),
                "CHECK_IN", "WEB", "");
        when(reservaDAO.findById(1)).thenReturn(Optional.of(reserva));
        when(reservaDAO.updateEstado(1, "FINALIZADA")).thenReturn(true);

        boolean ok = recepcionService.registrarCheckout(1, new BigDecimal("45.00"), "Todo conforme", 2);

        assertTrue(ok);
        verify(habitacionDAO).updateEstado(2, "DISPONIBLE");
        verify(reservaDAO).updateEstado(1, "FINALIZADA");
    }
}
