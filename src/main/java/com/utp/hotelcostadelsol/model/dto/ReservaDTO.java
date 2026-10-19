package com.utp.hotelcostadelsol.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * DTO que representa una reserva hotelera en el alcance del 70% (APF3),
 * incluyendo informacion del huesped, habitacion asignada, fechas de estancia y montos.
 */
public class ReservaDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer idReserva;
    private String codigoReserva;
    private Integer idCliente;
    private String nombreCliente;
    private String emailCliente;
    private Integer idTipoHabitacion;
    private String nombreTipoHabitacion;
    private Integer idHabitacion;
    private String numeroHabitacion;
    private LocalDate fechaCheckin;
    private LocalDate fechaCheckout;
    private Integer numeroHuespedes;
    private Boolean solicitaFactura;
    private String comentarios;
    private BigDecimal precioAlojamiento;
    private BigDecimal precioServiciosExtra;
    private BigDecimal montoTotal;
    private String estado;
    private String origen;
    private LocalDateTime fechaCreacion;

    public ReservaDTO() {
        this.numeroHuespedes = 1;
        this.solicitaFactura = Boolean.FALSE;
        this.precioAlojamiento = BigDecimal.ZERO;
        this.precioServiciosExtra = BigDecimal.ZERO;
        this.montoTotal = BigDecimal.ZERO;
        this.estado = "PENDIENTE";
        this.origen = "WEB";
        this.fechaCreacion = LocalDateTime.now();
    }

    public ReservaDTO(Integer idReserva, String codigoReserva, Integer idCliente, String nombreCliente,
                      Integer idTipoHabitacion, String nombreTipoHabitacion, Integer idHabitacion,
                      String numeroHabitacion, LocalDate fechaCheckin, LocalDate fechaCheckout,
                      Integer numeroHuespedes, BigDecimal precioAlojamiento, BigDecimal precioServiciosExtra,
                      BigDecimal montoTotal, String estado, String origen, String comentarios) {
        this.idReserva = idReserva;
        this.codigoReserva = codigoReserva;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.idTipoHabitacion = idTipoHabitacion;
        this.nombreTipoHabitacion = nombreTipoHabitacion;
        this.idHabitacion = idHabitacion;
        this.numeroHabitacion = numeroHabitacion;
        this.fechaCheckin = fechaCheckin;
        this.fechaCheckout = fechaCheckout;
        this.numeroHuespedes = numeroHuespedes;
        this.solicitaFactura = Boolean.TRUE;
        this.precioAlojamiento = precioAlojamiento;
        this.precioServiciosExtra = precioServiciosExtra != null ? precioServiciosExtra : BigDecimal.ZERO;
        this.montoTotal = montoTotal;
        this.estado = estado;
        this.origen = origen;
        this.comentarios = comentarios;
        this.fechaCreacion = LocalDateTime.now();
    }

    public long getCantidadNoches() {
        if (fechaCheckin != null && fechaCheckout != null && fechaCheckout.isAfter(fechaCheckin)) {
            return ChronoUnit.DAYS.between(fechaCheckin, fechaCheckout);
        }
        return 0L;
    }

    public Integer getIdReserva() { return idReserva; }
    public void setIdReserva(Integer idReserva) { this.idReserva = idReserva; }
    public String getCodigoReserva() { return codigoReserva; }
    public void setCodigoReserva(String codigoReserva) { this.codigoReserva = codigoReserva; }
    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }
    public Integer getIdTipoHabitacion() { return idTipoHabitacion; }
    public void setIdTipoHabitacion(Integer idTipoHabitacion) { this.idTipoHabitacion = idTipoHabitacion; }
    public String getNombreTipoHabitacion() { return nombreTipoHabitacion; }
    public void setNombreTipoHabitacion(String nombreTipoHabitacion) { this.nombreTipoHabitacion = nombreTipoHabitacion; }
    public Integer getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(Integer idHabitacion) { this.idHabitacion = idHabitacion; }
    public String getNumeroHabitacion() { return numeroHabitacion; }
    public void setNumeroHabitacion(String numeroHabitacion) { this.numeroHabitacion = numeroHabitacion; }
    public LocalDate getFechaCheckin() { return fechaCheckin; }
    public void setFechaCheckin(LocalDate fechaCheckin) { this.fechaCheckin = fechaCheckin; }
    public LocalDate getFechaCheckout() { return fechaCheckout; }
    public void setFechaCheckout(LocalDate fechaCheckout) { this.fechaCheckout = fechaCheckout; }
    public Integer getNumeroHuespedes() { return numeroHuespedes; }
    public void setNumeroHuespedes(Integer numeroHuespedes) { this.numeroHuespedes = numeroHuespedes; }
    public Boolean getSolicitaFactura() { return solicitaFactura; }
    public void setSolicitaFactura(Boolean solicitaFactura) { this.solicitaFactura = solicitaFactura; }
    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }
    public BigDecimal getPrecioAlojamiento() { return precioAlojamiento; }
    public void setPrecioAlojamiento(BigDecimal precioAlojamiento) { this.precioAlojamiento = precioAlojamiento; }
    public BigDecimal getPrecioServiciosExtra() { return precioServiciosExtra; }
    public void setPrecioServiciosExtra(BigDecimal precioServiciosExtra) { this.precioServiciosExtra = precioServiciosExtra; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
