package com.utp.hotelcostadelsol.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO que encapsula el resultado del calculo de cotizacion de alojamiento y servicios.
 */
public class CotizacionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idTipoHabitacion;
    private String nombreTipoHabitacion;
    private long noches;
    private BigDecimal precioBaseNoche;
    private BigDecimal subtotalAlojamiento;
    private BigDecimal cargoServiciosExtra;
    private BigDecimal montoTotal;

    public CotizacionDTO() {
        this.precioBaseNoche = BigDecimal.ZERO;
        this.subtotalAlojamiento = BigDecimal.ZERO;
        this.cargoServiciosExtra = BigDecimal.ZERO;
        this.montoTotal = BigDecimal.ZERO;
    }

    public CotizacionDTO(int idTipoHabitacion, String nombreTipoHabitacion, long noches,
                         BigDecimal precioBaseNoche, BigDecimal subtotalAlojamiento,
                         BigDecimal cargoServiciosExtra, BigDecimal montoTotal) {
        this.idTipoHabitacion = idTipoHabitacion;
        this.nombreTipoHabitacion = nombreTipoHabitacion;
        this.noches = noches;
        this.precioBaseNoche = precioBaseNoche;
        this.subtotalAlojamiento = subtotalAlojamiento;
        this.cargoServiciosExtra = cargoServiciosExtra;
        this.montoTotal = montoTotal;
    }

    public int getIdTipoHabitacion() { return idTipoHabitacion; }
    public void setIdTipoHabitacion(int idTipoHabitacion) { this.idTipoHabitacion = idTipoHabitacion; }
    public String getNombreTipoHabitacion() { return nombreTipoHabitacion; }
    public void setNombreTipoHabitacion(String nombreTipoHabitacion) { this.nombreTipoHabitacion = nombreTipoHabitacion; }
    public long getNoches() { return noches; }
    public void setNoches(long noches) { this.noches = noches; }
    public BigDecimal getPrecioBaseNoche() { return precioBaseNoche; }
    public void setPrecioBaseNoche(BigDecimal precioBaseNoche) { this.precioBaseNoche = precioBaseNoche; }
    public BigDecimal getSubtotalAlojamiento() { return subtotalAlojamiento; }
    public void setSubtotalAlojamiento(BigDecimal subtotalAlojamiento) { this.subtotalAlojamiento = subtotalAlojamiento; }
    public BigDecimal getCargoServiciosExtra() { return cargoServiciosExtra; }
    public void setCargoServiciosExtra(BigDecimal cargoServiciosExtra) { this.cargoServiciosExtra = cargoServiciosExtra; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
}
