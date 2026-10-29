package com.utp.hotelcostadelsol.bean;

import com.utp.hotelcostadelsol.model.dto.AuditoriaDTO;
import com.utp.hotelcostadelsol.model.dto.EstanciaDTO;
import com.utp.hotelcostadelsol.model.dto.HabitacionDTO;
import com.utp.hotelcostadelsol.model.dto.PagoDTO;
import com.utp.hotelcostadelsol.model.dto.ReservaDTO;
import com.utp.hotelcostadelsol.model.dto.UsuarioDTO;
import com.utp.hotelcostadelsol.service.HabitacionService;
import com.utp.hotelcostadelsol.service.RecepcionService;
import com.utp.hotelcostadelsol.service.ReservaService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Managed Bean (@ViewScoped) para el modulo de Recepcion: Check-in, Check-out,
 * Verificacion de Pagos y Bitacora de Auditoria (Proyecto Integrador).
 */
@Named("recepcionBean")
@ViewScoped
public class RecepcionBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(RecepcionBean.class);

    private List<ReservaDTO> reservasParaCheckin;
    private List<ReservaDTO> reservasEnCheckin;
    private List<ReservaDTO> reservasPendientesPago;
    private List<HabitacionDTO> habitacionesDisponibles;
    private List<EstanciaDTO> listaEstancias;
    private List<PagoDTO> listaPagos;
    private List<AuditoriaDTO> listaAuditoria;

    // Formulario Check-in
    private Integer idReservaCheckin;
    private Integer idHabitacionCheckin;
    private String llaveAsignada = "KEY-RFID-201A";

    // Formulario Check-out
    private Integer idReservaCheckout;
    private BigDecimal consumosAdicionales = BigDecimal.ZERO;
    private String observacionesCheckout = "Habitación entregada conforme sin daños";

    // Formulario Pago
    private Integer idReservaPago;
    private BigDecimal montoPago = new BigDecimal("540.00");
    private String metodoPago = "TARJETA_CREDITO";
    private String numeroOperacion = "OP-884421";

    private final RecepcionService recepcionService = new RecepcionService();
    private final ReservaService reservaService = new ReservaService();
    private final HabitacionService habitacionService = new HabitacionService();

    @PostConstruct
    public void init() {
        cargarDatos();
    }

    public void cargarDatos() {
        List<ReservaDTO> todas = reservaService.listarTodas();
        this.reservasParaCheckin = todas.stream()
                .filter(r -> "CONFIRMADA".equalsIgnoreCase(r.getEstado()) || "PENDIENTE".equalsIgnoreCase(r.getEstado()))
                .collect(Collectors.toList());
        this.reservasEnCheckin = todas.stream()
                .filter(r -> "CHECK_IN".equalsIgnoreCase(r.getEstado()))
                .collect(Collectors.toList());
        this.reservasPendientesPago = todas.stream()
                .filter(r -> "PENDIENTE".equalsIgnoreCase(r.getEstado()) || "CONFIRMADA".equalsIgnoreCase(r.getEstado()))
                .collect(Collectors.toList());

        this.habitacionesDisponibles = habitacionService.listarDisponibles();
        this.listaEstancias = recepcionService.listarEstancias();
        this.listaPagos = recepcionService.listarPagos();
        this.listaAuditoria = recepcionService.listarAuditoriaReciente(15);
    }

    public void procesarCheckin() {
        try {
            if (idReservaCheckin == null || idHabitacionCheckin == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Atención", "Seleccione una reserva y una habitación disponible."));
                return;
            }
            EstanciaDTO est = recepcionService.registrarCheckin(idReservaCheckin, idHabitacionCheckin, llaveAsignada, obtenerIdActor());
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Check-in Exitoso",
                    "Huésped instalado en habitación " + est.getNumeroHabitacion() + " con llave " + est.getLlaveAsignada()));
            cargarDatos();
        } catch (Exception e) {
            LOGGER.warn("Error al procesar check-in: {}", e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en Check-in", e.getMessage()));
        }
    }

    public void procesarCheckout() {
        try {
            if (idReservaCheckout == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Atención", "Seleccione una estancia activa para realizar el Check-out."));
                return;
            }
            boolean ok = recepcionService.registrarCheckout(idReservaCheckout, consumosAdicionales, observacionesCheckout, obtenerIdActor());
            if (ok) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Check-out Finalizado",
                        "La estancia fue cerrada y la habitación quedó liberada como DISPONIBLE."));
                this.consumosAdicionales = BigDecimal.ZERO;
                cargarDatos();
            }
        } catch (Exception e) {
            LOGGER.warn("Error al procesar check-out: {}", e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en Check-out", e.getMessage()));
        }
    }

    public void procesarPago() {
        try {
            if (idReservaPago == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Atención", "Seleccione una reserva para verificar su pago."));
                return;
            }
            PagoDTO pago = recepcionService.verificarPago(idReservaPago, montoPago, metodoPago, numeroOperacion, null, obtenerIdActor());
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Pago Verificado",
                    "Comprobante registrado por S/ " + pago.getMonto() + " (" + pago.getMetodoPago() + ")"));
            cargarDatos();
        } catch (Exception e) {
            LOGGER.warn("Error al verificar pago: {}", e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error en Pago", e.getMessage()));
        }
    }

    private int obtenerIdActor() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc != null && fc.getExternalContext() != null) {
            Object user = fc.getExternalContext().getSessionMap().get(LoginBean.SESSION_USER_KEY);
            if (user instanceof UsuarioDTO u && u.getIdUsuario() > 0) {
                return u.getIdUsuario();
            }
        }
        return 2;
    }

    public List<ReservaDTO> getReservasParaCheckin() { return reservasParaCheckin; }
    public List<ReservaDTO> getReservasEnCheckin() { return reservasEnCheckin; }
    public List<ReservaDTO> getReservasPendientesPago() { return reservasPendientesPago; }
    public List<HabitacionDTO> getHabitacionesDisponibles() { return habitacionesDisponibles; }
    public List<EstanciaDTO> getListaEstancias() { return listaEstancias; }
    public List<PagoDTO> getListaPagos() { return listaPagos; }
    public List<AuditoriaDTO> getListaAuditoria() { return listaAuditoria; }
    public Integer getIdReservaCheckin() { return idReservaCheckin; }
    public void setIdReservaCheckin(Integer idReservaCheckin) { this.idReservaCheckin = idReservaCheckin; }
    public Integer getIdHabitacionCheckin() { return idHabitacionCheckin; }
    public void setIdHabitacionCheckin(Integer idHabitacionCheckin) { this.idHabitacionCheckin = idHabitacionCheckin; }
    public String getLlaveAsignada() { return llaveAsignada; }
    public void setLlaveAsignada(String llaveAsignada) { this.llaveAsignada = llaveAsignada; }
    public Integer getIdReservaCheckout() { return idReservaCheckout; }
    public void setIdReservaCheckout(Integer idReservaCheckout) { this.idReservaCheckout = idReservaCheckout; }
    public BigDecimal getConsumosAdicionales() { return consumosAdicionales; }
    public void setConsumosAdicionales(BigDecimal consumosAdicionales) { this.consumosAdicionales = consumosAdicionales; }
    public String getObservacionesCheckout() { return observacionesCheckout; }
    public void setObservacionesCheckout(String observacionesCheckout) { this.observacionesCheckout = observacionesCheckout; }
    public Integer getIdReservaPago() { return idReservaPago; }
    public void setIdReservaPago(Integer idReservaPago) { this.idReservaPago = idReservaPago; }
    public BigDecimal getMontoPago() { return montoPago; }
    public void setMontoPago(BigDecimal montoPago) { this.montoPago = montoPago; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getNumeroOperacion() { return numeroOperacion; }
    public void setNumeroOperacion(String numeroOperacion) { this.numeroOperacion = numeroOperacion; }
}
