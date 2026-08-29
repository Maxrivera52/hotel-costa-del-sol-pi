package com.utp.hotelcostadelsol.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class TipoHabitacionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idTipo;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precioBaseNoche;
    private int capacidadMaxima;
    private String imagenUrl;
    private boolean activo;

    public TipoHabitacionDTO() {}

    public TipoHabitacionDTO(int idTipo, String codigo, String nombre, String descripcion, BigDecimal precioBaseNoche, int capacidadMaxima) {
        this.idTipo = idTipo;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioBaseNoche = precioBaseNoche;
        this.capacidadMaxima = capacidadMaxima;
        this.activo = true;
    }

    public int getIdTipo() { return idTipo; }
    public void setIdTipo(int idTipo) { this.idTipo = idTipo; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getPrecioBaseNoche() { return precioBaseNoche; }
    public void setPrecioBaseNoche(BigDecimal precioBaseNoche) { this.precioBaseNoche = precioBaseNoche; }
    public int getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(int capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}