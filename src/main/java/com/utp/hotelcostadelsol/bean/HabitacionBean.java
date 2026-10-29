package com.utp.hotelcostadelsol.bean;

import com.utp.hotelcostadelsol.model.dto.HabitacionDTO;
import com.utp.hotelcostadelsol.model.dto.TipoHabitacionDTO;
import com.utp.hotelcostadelsol.model.dto.UsuarioDTO;
import com.utp.hotelcostadelsol.security.Rol;
import com.utp.hotelcostadelsol.service.ExcelExportService;
import com.utp.hotelcostadelsol.service.HabitacionService;
import com.utp.hotelcostadelsol.service.IReporteExportService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Managed Bean (@ViewScoped) para la gestion del inventario de habitaciones
 * y exportacion de reportes en formato Microsoft Excel (.xlsx) mediante Apache POI,
 * aplicando restricciones de seguridad RBAC por rol.
 */
@Named("habitacionBean")
@ViewScoped
public class HabitacionBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(HabitacionBean.class);

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
    private final IReporteExportService exportService = new ExcelExportService();

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
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc != null) {
            fc.addMessage(null, new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    "Filtro aplicado",
                    "Se encontraron " + listaHabitaciones.size() + " habitaciones."
            ));
        }
    }

    public void limpiarFiltros() {
        this.filtroEstado = "";
        this.filtroTipo = 0;
        cargarDatos();
    }

    public void guardarHabitacion() {
        if (!tieneRolAdmin()) {
            agregarMensajeError("Acceso Denegado (RBAC)", "Solo el rol Administrador puede registrar nuevas habitaciones.");
            return;
        }
        if (StringUtils.isBlank(nuevaHabitacion.getNumero())) {
            agregarMensajeError("Validación", "El número de habitación es obligatorio.");
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
            FacesContext fc = FacesContext.getCurrentInstance();
            if (fc != null) {
                fc.addMessage(null, new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "Registro Exitoso",
                        "Habitación " + nuevaHabitacion.getNumero() + " guardada en base de datos."
                ));
            }
            this.nuevaHabitacion = new HabitacionDTO();
            this.nuevaHabitacion.setPrecioPorNoche(new BigDecimal("150.00"));
            this.nuevaHabitacion.setPiso(1);
            this.nuevaHabitacion.setIdTipo(1);
            cargarDatos();
        } else {
            agregarMensajeError("Error", "No se pudo registrar la habitación.");
        }
    }

    public void cambiarEstado(HabitacionDTO hab, String nuevoEstado) {
        if (!tieneRolPersonalOperativo()) {
            agregarMensajeError("Acceso Denegado (RBAC)", "No cuenta con permisos para modificar el estado de las habitaciones.");
            return;
        }
        boolean ok = habitacionService.actualizarEstado(hab.getIdHabitacion(), nuevoEstado);
        if (ok) {
            hab.setEstado(nuevoEstado);
            cargarDatos();
            FacesContext fc = FacesContext.getCurrentInstance();
            if (fc != null) {
                fc.addMessage(null, new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "Estado Actualizado",
                        "Habitación " + hab.getNumero() + " cambió a estado " + nuevoEstado
                ));
            }
        }
    }

    /**
     * Descarga el inventario actual de habitaciones en formato Excel (.xlsx) usando Apache POI.
     */
    public void exportarExcel() {
        if (!tieneRolPersonalOperativo()) {
            agregarMensajeError("Acceso Denegado (RBAC)", "Solo el personal autorizado puede exportar el inventario Excel.");
            return;
        }
        try {
            byte[] excelBytes = exportService.exportarInventarioHabitacionesExcel(listaHabitaciones);
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            ec.responseReset();
            ec.setResponseContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            ec.setResponseContentLength(excelBytes.length);
            ec.setResponseHeader("Content-Disposition", "attachment; filename=\"Inventario_Habitaciones_CostaDelSol.xlsx\"");

            try (OutputStream out = ec.getResponseOutputStream()) {
                out.write(excelBytes);
                out.flush();
            }
            fc.responseComplete();
        } catch (Exception e) {
            LOGGER.error("Error al exportar Excel de habitaciones: {}", e.getMessage());
            agregarMensajeError("Error Apache POI", "No se pudo generar el archivo Excel.");
        }
    }

    private UsuarioDTO obtenerUsuarioSesion() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc == null || fc.getExternalContext() == null) {
            return null;
        }
        Object user = fc.getExternalContext().getSessionMap().get(LoginBean.SESSION_USER_KEY);
        return (user instanceof UsuarioDTO u) ? u : null;
    }

    private boolean tieneRolAdmin() {
        UsuarioDTO u = obtenerUsuarioSesion();
        if (u == null) {
            return false;
        }
        return Rol.ROLE_ADMIN == Rol.fromCodigo(u.getRolCodigo()) || u.getIdRol() == Rol.ROLE_ADMIN.getIdRol();
    }

    private boolean tieneRolPersonalOperativo() {
        UsuarioDTO u = obtenerUsuarioSesion();
        if (u == null) {
            return false;
        }
        Rol r = Rol.fromCodigo(u.getRolCodigo());
        return r == Rol.ROLE_ADMIN || r == Rol.ROLE_RECEPCION
                || u.getIdRol() == Rol.ROLE_ADMIN.getIdRol()
                || u.getIdRol() == Rol.ROLE_RECEPCION.getIdRol();
    }

    private void agregarMensajeError(String resumen, String detalle) {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc != null) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, resumen, detalle));
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
