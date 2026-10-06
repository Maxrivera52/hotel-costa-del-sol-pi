package com.utp.hotelcostadelsol.bean;

import com.utp.hotelcostadelsol.model.dto.HabitacionDTO;
import com.utp.hotelcostadelsol.model.dto.TipoHabitacionDTO;
import com.utp.hotelcostadelsol.service.HabitacionService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Named("habitacionBean")
@ViewScoped
public class HabitacionBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<HabitacionDTO> listaHabitaciones;
    private List<TipoHabitacionDTO> listaTipos;

    private String filtroEstado = "";
    private Integer filtroTipo = 0;

    private HabitacionDTO nuevaHabitacion;

    private int totalHabitaciones;
    private int disponibles;
    private int ocupadas;
    private int mantenimiento;

    private final HabitacionService habitacionService = new HabitacionService();

    @PostConstruct
    public void init() {
        this.nuevaHabitacion = new HabitacionDTO();
        this.nuevaHabitacion.setPrecioPorNoche(new BigDecimal("150.00"));
        this.nuevaHabitacion.setPiso(1);
        this.nuevaHabitacion.setIdTipo(1);
        cargarDatos();
    }

    public void cargarDatos() {
        this.listaHabitaciones = habitacionService.listarPorFiltro(filtroEstado, filtroTipo);
        this.listaTipos = habitacionService.listarTipos();

        Map<String, Integer> stats = habitacionService.obtenerResumenEstados();
        this.totalHabitaciones = stats.getOrDefault("TOTAL", 0);
        this.disponibles = stats.getOrDefault("DISPONIBLE", 0);
        this.ocupadas = stats.getOrDefault("OCUPADA", 0);
        this.mantenimiento = stats.getOrDefault("MANTENIMIENTO", 0);
    }

    public void filtrar() {
        this.listaHabitaciones = habitacionService.listarPorFiltro(filtroEstado, filtroTipo);
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Filtro aplicado", "Se encontraron " + listaHabitaciones.size() + " habitaciones."));
    }

    public void limpiarFiltros() {
        this.filtroEstado = "";
        this.filtroTipo = 0;
        cargarDatos();
    }

    public void guardarHabitacion() {
        if (nuevaHabitacion.getNumero() == null || nuevaHabitacion.getNumero().trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validacion", "El numero de habitacion es obligatorio."));
            return;
        }

        for (TipoHabitacionDTO t : listaTipos) {
            if (t.getIdTipo() == nuevaHabitacion.getIdTipo()) {
                nuevaHabitacion.setTipoNombre(t.getNombre());
                break;
            }
        }

        boolean exito = habitacionService.registrarHabitacion(nuevaHabitacion);
        if (exito) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Registro Exitoso", "Habitacion " + nuevaHabitacion.getNumero() + " guardada en base de datos."));
            this.nuevaHabitacion = new HabitacionDTO();
            this.nuevaHabitacion.setPrecioPorNoche(new BigDecimal("150.00"));
            this.nuevaHabitacion.setPiso(1);
            this.nuevaHabitacion.setIdTipo(1);
            cargarDatos();
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar la habitacion."));
        }
    }

    public void cambiarEstado(HabitacionDTO hab, String nuevoEstado) {
        boolean ok = habitacionService.actualizarEstado(hab.getIdHabitacion(), nuevoEstado);
        if (ok) {
            hab.setEstado(nuevoEstado);
            cargarDatos();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Estado Actualizado", "Habitacion " + hab.getNumero() + " cambio a estado " + nuevoEstado));
        }
    }

    public List<HabitacionDTO> getListaHabitaciones() { return listaHabitaciones; }
    public List<TipoHabitacionDTO> getListaTipos() { return listaTipos; }
    public String getFiltroEstado() { return filtroEstado; }
    public void setFiltroEstado(String filtroEstado) { this.filtroEstado = filtroEstado; }
    public Integer getFiltroTipo() { return filtroTipo; }
    public void setFiltroTipo(Integer filtroTipo) { this.filtroTipo = filtroTipo; }
    public HabitacionDTO getNuevaHabitacion() { return nuevaHabitacion; }
    public void setNuevaHabitacion(HabitacionDTO nuevaHabitacion) { this.nuevaHabitacion = nuevaHabitacion; }
    public int getTotalHabitaciones() { return totalHabitaciones; }
    public int getDisponibles() { return disponibles; }
    public int getOcupadas() { return ocupadas; }
    public int getMantenimiento() { return mantenimiento; }
}