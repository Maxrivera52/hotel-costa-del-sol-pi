package com.utp.hotelcostadelsol.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO que representa el ciclo de estancia presencial del huesped (Check-in y Check-out).
 */
public class EstanciaDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Integer idEstancia;
    private Integer idReserva;
    private String codigoReserva;
    private String nombreHuesped;
    private String numeroHabitacion;
    private LocalDateTime fechaHoraCheckin;
    private LocalDateTime fechaHoraCheckout;
    private String llaveAsignada;
    private BigDecimal consumosAdicionales;
    private String observaciones;
    private Integer idRecepcionistaCheckin;
    private Integer idRecepcionistaCheckout;

    public EstanciaDTO() {
        this.fechaHoraCheckin = LocalDateTime.now();
        this.consumosAdicionales = BigDecimal.ZERO;
    }

    public EstanciaDTO(Integer idEstancia, Integer idReserva, String codigoReserva, String nombreHuesped,
                       String numeroHabitacion, LocalDateTime fechaHoraCheckin, LocalDateTime fechaHoraCheckout,
                       String llaveAsignada, BigDecimal consumosAdicionales, String observaciones) {
        this.idEstancia = idEstancia;
        this.idReserva = idReserva;
        this.codigoReserva = codigoReserva;
        this.nombreHuesped = nombreHuesped;
        this.numeroHabitacion = numeroHabitacion;
        this.fechaHoraCheckin = fechaHoraCheckin;
        this.fechaHoraCheckout = fechaHoraCheckout;
        this.llaveAsignada = llaveAsignada;
        this.consumosAdicionales = consumosAdicionales != null ? consumosAdicionales : BigDecimal.ZERO;
        this.observaciones = observaciones;
    }

    public String getCheckinFormateado() {
        return fechaHoraCheckin != null ? fechaHoraCheckin.format(FMT) : "-";
    }

    public String getCheckoutFormateado() {
        return fechaHoraCheckout != null ? fechaHoraCheckout.format(FMT) : "En curso";
    }

    public Integer getIdEstancia() { return idEstancia; }
    public void setIdEstancia(Integer idEstancia) { this.idEstancia = idEstancia; }
    public Integer getIdReserva() { return idReserva; }
    public void setIdReserva(Integer idReserva) { this.idReserva = idReserva; }
    public String getCodigoReserva() { return codigoReserva; }
    public void setCodigoReserva(String codigoReserva) { this.codigoReserva = codigoReserva; }
    public String getNombreHuesped() { return nombreHuesped; }
    public void setNombreHuesped(String nombreHuesped) { this.nombreHuesped = nombreHuesped; }
    public String getNumeroHabitacion() { return numeroHabitacion; }
    public void setNumeroHabitacion(String numeroHabitacion) { this.numeroHabitacion = numeroHabitacion; }
    public LocalDateTime getFechaHoraCheckin() { return fechaHoraCheckin; }
    public void setFechaHoraCheckin(LocalDateTime fechaHoraCheckin) { this.fechaHoraCheckin = fechaHoraCheckin; }
    public LocalDateTime getFechaHoraCheckout() { return fechaHoraCheckout; }
    public void setFechaHoraCheckout(LocalDateTime fechaHoraCheckout) { this.fechaHoraCheckout = fechaHoraCheckout; }
    public String getLlaveAsignada() { return llaveAsignada; }
    public void setLlaveAsignada(String llaveAsignada) { this.llaveAsignada = llaveAsignada; }
    public BigDecimal getConsumosAdicionales() { return consumosAdicionales; }
    public void setConsumosAdicionales(BigDecimal consumosAdicionales) { this.consumosAdicionales = consumosAdicionales; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Integer getIdRecepcionistaCheckin() { return idRecepcionistaCheckin; }
    public void setIdRecepcionistaCheckin(Integer idRecepcionistaCheckin) { this.idRecepcionistaCheckin = idRecepcionistaCheckin; }
    public Integer getIdRecepcionistaCheckout() { return idRecepcionistaCheckout; }
    public void setIdRecepcionistaCheckout(Integer idRecepcionistaCheckout) { this.idRecepcionistaCheckout = idRecepcionistaCheckout; }
}
