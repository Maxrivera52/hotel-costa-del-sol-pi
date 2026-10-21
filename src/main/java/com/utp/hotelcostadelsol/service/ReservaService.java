package com.utp.hotelcostadelsol.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.utp.hotelcostadelsol.dao.AuditoriaDAO;
import com.utp.hotelcostadelsol.dao.HabitacionDAO;
import com.utp.hotelcostadelsol.dao.IHabitacionDAO;
import com.utp.hotelcostadelsol.dao.IReservaDAO;
import com.utp.hotelcostadelsol.dao.ReservaDAO;
import com.utp.hotelcostadelsol.dao.TipoHabitacionDAO;
import com.utp.hotelcostadelsol.model.dto.AuditoriaDTO;
import com.utp.hotelcostadelsol.model.dto.CotizacionDTO;
import com.utp.hotelcostadelsol.model.dto.HabitacionDTO;
import com.utp.hotelcostadelsol.model.dto.ReservaDTO;
import com.utp.hotelcostadelsol.model.dto.TipoHabitacionDTO;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio de negocio que orquesta la cotizacion dinamica y el ciclo de vida de reservas.
 * Utiliza Google Guava (Preconditions, ImmutableMap), Apache Commons Lang3 (RandomStringUtils, StringUtils)
 * y Logback (SLF4J) bajo principios de arquitectura SOLID.
 */
public class ReservaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservaService.class);

    private final IReservaDAO reservaDAO;
    private final TipoHabitacionDAO tipoHabitacionDAO;
    private final IHabitacionDAO habitacionDAO;
    private final AuditoriaDAO auditoriaDAO;

    public ReservaService() {
        this(new ReservaDAO(), new TipoHabitacionDAO(), new HabitacionDAO(), new AuditoriaDAO());
    }

    public ReservaService(IReservaDAO reservaDAO, TipoHabitacionDAO tipoHabitacionDAO,
                          IHabitacionDAO habitacionDAO, AuditoriaDAO auditoriaDAO) {
        this.reservaDAO = Preconditions.checkNotNull(reservaDAO, "IReservaDAO requerido");
        this.tipoHabitacionDAO = Preconditions.checkNotNull(tipoHabitacionDAO, "TipoHabitacionDAO requerido");
        this.habitacionDAO = Preconditions.checkNotNull(habitacionDAO, "IHabitacionDAO requerido");
        this.auditoriaDAO = Preconditions.checkNotNull(auditoriaDAO, "AuditoriaDAO requerido");
    }

    /**
     * Calcula la cotizacion de alojamiento y servicios adicionales validando rangos de fechas con Google Guava.
     *
     * @param idTipoHabitacion   ID de categoria de habitacion
     * @param checkin            fecha de entrada
     * @param checkout           fecha de salida
     * @param cargoServiciosExtra monto opcional de servicios adicionales
     * @return CotizacionDTO con el desglose financiero
     */
    public CotizacionDTO calcularCotizacion(int idTipoHabitacion, LocalDate checkin, LocalDate checkout,
                                            BigDecimal cargoServiciosExtra) {
        validarFechas(checkin, checkout);

        TipoHabitacionDTO tipo = tipoHabitacionDAO.findById(idTipoHabitacion)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de habitacion no encontrado con ID: " + idTipoHabitacion));

        long noches = ChronoUnit.DAYS.between(checkin, checkout);
        BigDecimal precioBase = tipo.getPrecioBaseNoche();
        BigDecimal subtotalAlojamiento = precioBase.multiply(BigDecimal.valueOf(noches));
        BigDecimal extra = (cargoServiciosExtra != null && cargoServiciosExtra.compareTo(BigDecimal.ZERO) > 0)
                ? cargoServiciosExtra : BigDecimal.ZERO;
        BigDecimal total = subtotalAlojamiento.add(extra);

        LOGGER.info("Cotizacion generada: Tipo={} | Noches={} | Total=S/ {}", tipo.getNombre(), noches, total);
        return new CotizacionDTO(idTipoHabitacion, tipo.getNombre(), noches, precioBase, subtotalAlojamiento, extra, total);
    }

    /**
     * Registra una nueva reserva generando un codigo alfanumerico unico con Apache Commons RandomStringUtils.
     *
     * @param reserva DTO de la reserva a persistir
     * @return ReservaDTO persistida con su codigo e ID
     */
    public ReservaDTO crearReserva(ReservaDTO reserva) {
        Preconditions.checkNotNull(reserva, "La reserva no puede ser nula");
        validarFechas(reserva.getFechaCheckin(), reserva.getFechaCheckout());
        Preconditions.checkArgument(reserva.getNumeroHuespedes() != null && reserva.getNumeroHuespedes() >= 1,
                "El numero de huespedes debe ser al menos 1");
        Preconditions.checkArgument(reserva.getIdTipoHabitacion() != null && reserva.getIdTipoHabitacion() > 0,
                "Debe seleccionar un tipo de habitacion valido");

        CotizacionDTO cot = calcularCotizacion(
                reserva.getIdTipoHabitacion(),
                reserva.getFechaCheckin(),
                reserva.getFechaCheckout(),
                reserva.getPrecioServiciosExtra()
        );

        reserva.setNombreTipoHabitacion(cot.getNombreTipoHabitacion());
        reserva.setPrecioAlojamiento(cot.getSubtotalAlojamiento());
        reserva.setPrecioServiciosExtra(cot.getCargoServiciosExtra());
        reserva.setMontoTotal(cot.getMontoTotal());

        if (reserva.getIdHabitacion() != null && reserva.getIdHabitacion() > 0) {
            Optional<HabitacionDTO> optHab = habitacionDAO.findById(reserva.getIdHabitacion());
            if (optHab.isPresent()) {
                HabitacionDTO hab = optHab.get();
                Preconditions.checkState(!"MANTENIMIENTO".equalsIgnoreCase(hab.getEstado()),
                        "La habitacion seleccionada se encuentra en mantenimiento");
                reserva.setNumeroHabitacion(hab.getNumero());
            }
        }

        // Uso de Apache Commons Lang3 RandomStringUtils para generar codigo unico WYN-2026-XXXXXX
        if (StringUtils.isBlank(reserva.getCodigoReserva())) {
            String sufijo = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
            reserva.setCodigoReserva("WYN-2026-" + sufijo);
        }

        reserva.setNombreCliente(StringUtils.defaultIfBlank(reserva.getNombreCliente(), "Huesped Registrado"));
        reserva.setEstado(StringUtils.defaultIfBlank(reserva.getEstado(), "PENDIENTE"));
        reserva.setOrigen(StringUtils.defaultIfBlank(reserva.getOrigen(), "WEB"));

        ReservaDTO guardada = reservaDAO.save(reserva);

        AuditoriaDTO audit = new AuditoriaDTO();
        audit.setIdUsuario(guardada.getIdCliente());
        audit.setModulo("RESERVAS");
        audit.setAccion("CREAR_RESERVA");
        audit.setDescripcion("Reserva " + guardada.getCodigoReserva() + " registrada por S/ " + guardada.getMontoTotal());
        auditoriaDAO.registrarEvento(audit);

        return guardada;
    }

    /**
     * Cancela una reserva activa y registra el evento en auditoria.
     *
     * @param idReserva ID de la reserva
     * @param idUsuario ID del usuario que solicita la cancelacion
     * @return true si fue cancelada exitosamente
     */
    public boolean cancelarReserva(int idReserva, int idUsuario) {
        Optional<ReservaDTO> opt = reservaDAO.findById(idReserva);
        if (opt.isEmpty()) {
            return false;
        }
        ReservaDTO r = opt.get();
        if ("CANCELADA".equalsIgnoreCase(r.getEstado()) || "FINALIZADA".equalsIgnoreCase(r.getEstado())) {
            LOGGER.warn("La reserva {} no puede cancelarse porque esta en estado {}", r.getCodigoReserva(), r.getEstado());
            return false;
        }

        boolean ok = reservaDAO.updateEstado(idReserva, "CANCELADA");
        if (ok) {
            AuditoriaDTO audit = new AuditoriaDTO();
            audit.setIdUsuario(idUsuario);
            audit.setModulo("RESERVAS");
            audit.setAccion("CANCELAR_RESERVA");
            audit.setDescripcion("Reserva " + r.getCodigoReserva() + " cancelada.");
            auditoriaDAO.registrarEvento(audit);
        }
        return ok;
    }

    public List<ReservaDTO> listarTodas() {
        return reservaDAO.findAll();
    }

    public List<ReservaDTO> listarPorEstado(String estado) {
        return reservaDAO.findByEstado(estado);
    }

    public BigDecimal obtenerTotalIngresosActivos() {
        return reservaDAO.findAll().stream()
                .filter(r -> !"CANCELADA".equalsIgnoreCase(r.getEstado()))
                .map(r -> r.getMontoTotal() != null ? r.getMontoTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<String, Integer> contarPorEstado() {
        int pendientes = 0;
        int confirmadas = 0;
        int enCheckin = 0;
        int finalizadas = 0;

        List<ReservaDTO> todas = reservaDAO.findAll();
        for (ReservaDTO r : todas) {
            String st = StringUtils.defaultString(r.getEstado());
            if ("PENDIENTE".equalsIgnoreCase(st)) pendientes++;
            else if ("CONFIRMADA".equalsIgnoreCase(st)) confirmadas++;
            else if ("CHECK_IN".equalsIgnoreCase(st)) enCheckin++;
            else if ("FINALIZADA".equalsIgnoreCase(st)) finalizadas++;
        }

        return ImmutableMap.of(
                "TOTAL", todas.size(),
                "PENDIENTE", pendientes,
                "CONFIRMADA", confirmadas,
                "CHECK_IN", enCheckin,
                "FINALIZADA", finalizadas
        );
    }

    private void validarFechas(LocalDate checkin, LocalDate checkout) {
        Preconditions.checkNotNull(checkin, "La fecha de check-in es obligatoria");
        Preconditions.checkNotNull(checkout, "La fecha de check-out es obligatoria");
        Preconditions.checkArgument(checkout.isAfter(checkin),
                "La fecha de check-out debe ser posterior a la fecha de check-in");
    }
}
