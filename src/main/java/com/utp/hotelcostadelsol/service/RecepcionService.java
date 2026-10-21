package com.utp.hotelcostadelsol.service;

import com.google.common.base.Preconditions;
import com.utp.hotelcostadelsol.dao.AuditoriaDAO;
import com.utp.hotelcostadelsol.dao.EstanciaDAO;
import com.utp.hotelcostadelsol.dao.HabitacionDAO;
import com.utp.hotelcostadelsol.dao.IHabitacionDAO;
import com.utp.hotelcostadelsol.dao.IReservaDAO;
import com.utp.hotelcostadelsol.dao.PagoDAO;
import com.utp.hotelcostadelsol.dao.ReservaDAO;
import com.utp.hotelcostadelsol.model.dto.AuditoriaDTO;
import com.utp.hotelcostadelsol.model.dto.EstanciaDTO;
import com.utp.hotelcostadelsol.model.dto.HabitacionDTO;
import com.utp.hotelcostadelsol.model.dto.PagoDTO;
import com.utp.hotelcostadelsol.model.dto.ReservaDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de negocio para operaciones de Recepcion (Check-in, Check-out, Verificacion de Pagos y Auditoria).
 * Aplica principios SOLID (inyeccion de dependencias), Google Guava Preconditions, Apache Commons y Logback.
 */
public class RecepcionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecepcionService.class);

    private final IReservaDAO reservaDAO;
    private final IHabitacionDAO habitacionDAO;
    private final EstanciaDAO estanciaDAO;
    private final PagoDAO pagoDAO;
    private final AuditoriaDAO auditoriaDAO;

    public RecepcionService() {
        this(new ReservaDAO(), new HabitacionDAO(), new EstanciaDAO(), new PagoDAO(), new AuditoriaDAO());
    }

    public RecepcionService(IReservaDAO reservaDAO, IHabitacionDAO habitacionDAO,
                            EstanciaDAO estanciaDAO, PagoDAO pagoDAO, AuditoriaDAO auditoriaDAO) {
        this.reservaDAO = Preconditions.checkNotNull(reservaDAO, "IReservaDAO requerido");
        this.habitacionDAO = Preconditions.checkNotNull(habitacionDAO, "IHabitacionDAO requerido");
        this.estanciaDAO = Preconditions.checkNotNull(estanciaDAO, "EstanciaDAO requerido");
        this.pagoDAO = Preconditions.checkNotNull(pagoDAO, "PagoDAO requerido");
        this.auditoriaDAO = Preconditions.checkNotNull(auditoriaDAO, "AuditoriaDAO requerido");
    }

    /**
     * Verifica y registra un pago o voucher asociado a una reserva, cambiando su estado a CONFIRMADA.
     */
    public PagoDTO verificarPago(int idReserva, BigDecimal monto, String metodoPago,
                                 String numeroOperacion, String comprobanteVoucher, int idRecepcionista) {
        ReservaDTO reserva = reservaDAO.findById(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + idReserva));

        BigDecimal montoFinal = (monto != null && monto.compareTo(BigDecimal.ZERO) > 0)
                ? monto : reserva.getMontoTotal();

        PagoDTO pago = new PagoDTO();
        pago.setIdReserva(idReserva);
        pago.setCodigoReserva(reserva.getCodigoReserva());
        pago.setMonto(montoFinal);
        pago.setMetodoPago(StringUtils.defaultIfBlank(metodoPago, "EFECTIVO_VENTANILLA"));
        pago.setNumeroOperacion(StringUtils.defaultIfBlank(numeroOperacion, "OP-DIRECTO"));
        pago.setComprobanteVoucher(StringUtils.defaultIfBlank(comprobanteVoucher, "VOUCHER-" + reserva.getCodigoReserva() + ".pdf"));
        pago.setEstadoPago("VERIFICADO");
        pago.setIdRecepcionista(idRecepcionista);
        pago.setFechaPago(LocalDateTime.now());

        PagoDTO guardado = pagoDAO.save(pago);

        if ("PENDIENTE".equalsIgnoreCase(reserva.getEstado())) {
            reservaDAO.updateEstado(idReserva, "CONFIRMADA");
        }

        registrarAuditoria(idRecepcionista, "RECEPCION", "VERIFICAR_PAGO",
                "Pago verificado por S/ " + montoFinal + " (" + pago.getMetodoPago() + ") para reserva " + reserva.getCodigoReserva());

        LOGGER.info("Pago verificado exitosamente para reserva {}", reserva.getCodigoReserva());
        return guardado;
    }

    /**
     * Ejecuta el Check-in presencial del huesped: asigna habitacion fisica, la marca como OCUPADA,
     * entrega tarjeta/llave RFID y actualiza la reserva a CHECK_IN.
     */
    public EstanciaDTO registrarCheckin(int idReserva, int idHabitacion, String llaveAsignada, int idRecepcionista) {
        Preconditions.checkArgument(StringUtils.isNotBlank(llaveAsignada), "Debe indicar el codigo de llave RFID asignada");

        ReservaDTO reserva = reservaDAO.findById(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + idReserva));

        HabitacionDTO habitacion = habitacionDAO.findById(idHabitacion)
                .orElseThrow(() -> new IllegalArgumentException("Habitacion no encontrada con ID: " + idHabitacion));

        Preconditions.checkState(!"MANTENIMIENTO".equalsIgnoreCase(habitacion.getEstado()),
                "La habitacion " + habitacion.getNumero() + " se encuentra en mantenimiento");

        habitacionDAO.updateEstado(idHabitacion, "OCUPADA");
        reservaDAO.assignHabitacion(idReserva, idHabitacion, habitacion.getNumero());
        reservaDAO.updateEstado(idReserva, "CHECK_IN");

        EstanciaDTO estancia = new EstanciaDTO();
        estancia.setIdReserva(idReserva);
        estancia.setCodigoReserva(reserva.getCodigoReserva());
        estancia.setNombreHuesped(reserva.getNombreCliente());
        estancia.setNumeroHabitacion(habitacion.getNumero());
        estancia.setFechaHoraCheckin(LocalDateTime.now());
        estancia.setLlaveAsignada(StringUtils.trimToEmpty(llaveAsignada).toUpperCase());
        estancia.setConsumosAdicionales(BigDecimal.ZERO);
        estancia.setObservaciones("Check-in presencial verificado en recepcion");
        estancia.setIdRecepcionistaCheckin(idRecepcionista);

        EstanciaDTO guardada = estanciaDAO.save(estancia);

        registrarAuditoria(idRecepcionista, "RECEPCION", "CHECK_IN",
                "Check-in realizado para reserva " + reserva.getCodigoReserva() + " en habitacion " + habitacion.getNumero());

        return guardada;
    }

    /**
     * Ejecuta el Check-out del huesped: liquida consumos adicionales, libera la habitacion a DISPONIBLE
     * y cambia el estado de la reserva a FINALIZADA.
     */
    public boolean registrarCheckout(int idReserva, BigDecimal consumosAdicionales,
                                     String observaciones, int idRecepcionista) {
        ReservaDTO reserva = reservaDAO.findById(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + idReserva));

        BigDecimal cargos = (consumosAdicionales != null && consumosAdicionales.compareTo(BigDecimal.ZERO) >= 0)
                ? consumosAdicionales : BigDecimal.ZERO;

        estanciaDAO.registrarCheckout(idReserva, cargos, observaciones, idRecepcionista);

        if (reserva.getIdHabitacion() != null && reserva.getIdHabitacion() > 0) {
            habitacionDAO.updateEstado(reserva.getIdHabitacion(), "DISPONIBLE");
        }

        boolean ok = reservaDAO.updateEstado(idReserva, "FINALIZADA");

        registrarAuditoria(idRecepcionista, "RECEPCION", "CHECK_OUT",
                "Check-out finalizado para reserva " + reserva.getCodigoReserva() + " (Consumos extra: S/ " + cargos + ")");

        return ok;
    }

    public List<EstanciaDTO> listarEstancias() {
        return estanciaDAO.findAll();
    }

    public List<PagoDTO> listarPagos() {
        return pagoDAO.findAll();
    }

    public List<AuditoriaDTO> listarAuditoriaReciente(int limit) {
        return auditoriaDAO.listarRecientes(limit);
    }

    private void registrarAuditoria(int idUsuario, String modulo, String accion, String descripcion) {
        AuditoriaDTO audit = new AuditoriaDTO();
        audit.setIdUsuario(idUsuario);
        audit.setModulo(modulo);
        audit.setAccion(accion);
        audit.setDescripcion(descripcion);
        auditoriaDAO.registrarEvento(audit);
    }
}
