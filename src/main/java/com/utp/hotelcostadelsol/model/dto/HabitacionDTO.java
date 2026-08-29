package com.utp.hotelcostadelsol.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class HabitacionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idHabitacion;
    private int idTipo;
    private String numero;
    private int piso;
    private String tipoNombre;
    private String estado;
    private BigDecimal precioPorNoche;
    private String observaciones;

    public HabitacionDTO() {
        this.estado = "DISPONIBLE";
        this.precioPorNoche = BigDecimal.ZERO;
    }

    public HabitacionDTO(int idHabitacion, int idTipo, String numero, int piso, String tipoNombre, String estado, BigDecimal precioPorNoche, String observaciones) {
        this.idHabitacion = idHabitacion;
        this.idTipo = idTipo;
        this.numero = numero;
        this.piso = piso;
        this.tipoNombre = tipoNombre;
        this.estado = estado;
        this.precioPorNoche = precioPorNoche;
        this.observaciones = observaciones;
    }

    public int getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(int idHabitacion) { this.idHabitacion = idHabitacion; }
    public int getIdTipo() { return idTipo; }
    public void setIdTipo(int idTipo) { this.idTipo = idTipo; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public int getPiso() { return piso; }
    public void setPiso(int piso) { this.piso = piso; }
    public String getTipoNombre() { return tipoNombre; }
    public void setTipoNombre(String tipoNombre) { this.tipoNombre = tipoNombre; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public BigDecimal getPrecioPorNoche() { return precioPorNoche; }
    public void setPrecioPorNoche(BigDecimal precioPorNoche) { this.precioPorNoche = precioPorNoche; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}