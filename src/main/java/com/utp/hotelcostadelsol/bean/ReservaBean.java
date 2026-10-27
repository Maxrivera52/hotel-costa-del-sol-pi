package com.utp.hotelcostadelsol.bean;

import com.utp.hotelcostadelsol.model.dto.CotizacionDTO;
import com.utp.hotelcostadelsol.model.dto.HabitacionDTO;
import com.utp.hotelcostadelsol.model.dto.ReservaDTO;
import com.utp.hotelcostadelsol.model.dto.TipoHabitacionDTO;
import com.utp.hotelcostadelsol.service.ExcelExportService;
import com.utp.hotelcostadelsol.service.HabitacionService;
import com.utp.hotelcostadelsol.service.IReporteExportService;
import com.utp.hotelcostadelsol.service.ReservaService;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Managed Bean (@ViewScoped) para el modulo de Reservas, Cotizacion Dinamica
 * y Exportacion de Reportes Excel con Apache POI (Alcance 70% - APF3).
 */
@Named("reservaBean")
@ViewScoped
public class ReservaBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservaBean.class);

    private List<ReservaDTO> listaReservas;
    private List<TipoHabitacionDTO> listaTipos;
    private List<HabitacionDTO> habitacionesDisponibles;

    private String filtroEstado = "";
    private ReservaDTO nuevaReserva;
    private String fechaCheckinStr;
    private String fechaCheckoutStr;
    private CotizacionDTO cotizacionActual;

    private int totalReservas;
    private int pendientes;
    private int confirmadas;
    private int enCheckin;
    private BigDecimal ingresosTotales;

    private final ReservaService reservaService = new ReservaService();
    private final HabitacionService habitacionService = new HabitacionService();
    private final IReporteExportService exportService = new ExcelExportService();

    @PostConstruct
    public void init() {
        reiniciarFormulario();
        cargarDatos();
    }

    private void reiniciarFormulario() {
        this.nuevaReserva = new ReservaDTO();
        this.nuevaReserva.setIdCliente(3);
        this.nuevaReserva.setNombreCliente("Joel Cori Alarcón");
        this.nuevaReserva.setIdTipoHabitacion(2);
        this.nuevaReserva.setNumeroHuespedes(2);
        this.nuevaReserva.setPrecioServiciosExtra(BigDecimal.ZERO);
        this.fechaCheckinStr = LocalDate.now().plusDays(1).toString();
        this.fechaCheckoutStr = LocalDate.now().plusDays(3).toString();
        this.cotizacionActual = null;
    }

    public void cargarDatos() {
        this.listaReservas = reservaService.listarPorEstado(filtroEstado);
        this.listaTipos = habitacionService.listarTipos();
        this.habitacionesDisponibles = habitacionService.listarDisponibles();

        Map<String, Integer> stats = reservaService.contarPorEstado();
        this.totalReservas = stats.getOrDefault("TOTAL", 0);
        this.pendientes = stats.getOrDefault("PENDIENTE", 0);
        this.confirmadas = stats.getOrDefault("CONFIRMADA", 0);
        this.enCheckin = stats.getOrDefault("CHECK_IN", 0);
        this.ingresosTotales = reservaService.obtenerTotalIngresosActivos();
    }

    public void cotizar() {
        try {
            LocalDate ci = LocalDate.parse(StringUtils.trimToEmpty(fechaCheckinStr));
            LocalDate co = LocalDate.parse(StringUtils.trimToEmpty(fechaCheckoutStr));
            this.cotizacionActual = reservaService.calcularCotizacion(
                    nuevaReserva.getIdTipoHabitacion(), ci, co, nuevaReserva.getPrecioServiciosExtra()
            );
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Cotización Calculada",
                    "Estancia de " + cotizacionActual.getNoches() + " noche(s) - Total estimado: S/ " + cotizacionActual.getMontoTotal()));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validación Guava", e.getMessage()));
        }
    }

    public void guardarReserva() {
        try {
            LocalDate ci = LocalDate.parse(StringUtils.trimToEmpty(fechaCheckinStr));
            LocalDate co = LocalDate.parse(StringUtils.trimToEmpty(fechaCheckoutStr));
            nuevaReserva.setFechaCheckin(ci);
            nuevaReserva.setFechaCheckout(co);

            ReservaDTO creada = reservaService.crearReserva(nuevaReserva);
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Reserva Registrada",
                    "Código generado (Apache Commons): " + creada.getCodigoReserva() + " por S/ " + creada.getMontoTotal()));

            reiniciarFormulario();
            cargarDatos();
        } catch (Exception e) {
            LOGGER.warn("Error de validacion al crear reserva: {}", e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de Validación", e.getMessage()));
        }
    }

    public void cancelar(ReservaDTO reserva) {
        boolean ok = reservaService.cancelarReserva(reserva.getIdReserva(), 1);
        if (ok) {
            cargarDatos();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Reserva Cancelada",
                    "La reserva " + reserva.getCodigoReserva() + " cambió a estado CANCELADA."));
        }
    }

    public void filtrar() {
        this.listaReservas = reservaService.listarPorEstado(filtroEstado);
    }

    public void limpiarFiltros() {
        this.filtroEstado = "";
        cargarDatos();
    }

    /**
     * Exporta el reporte consolidado de reservas a un archivo Excel (.xlsx) con Apache POI.
     */
    public void exportarExcel() {
        try {
            byte[] excelBytes = exportService.exportarReporteReservasExcel(listaReservas);
            FacesContext fc = FacesContext.getCurrentInstance();
            ExternalContext ec = fc.getExternalContext();
            ec.responseReset();
            ec.setResponseContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            ec.setResponseContentLength(excelBytes.length);
            ec.setResponseHeader("Content-Disposition", "attachment; filename=\"Reporte_Reservas_CostaDelSol.xlsx\"");

            try (OutputStream out = ec.getResponseOutputStream()) {
                out.write(excelBytes);
                out.flush();
            }
            fc.responseComplete();
        } catch (Exception e) {
            LOGGER.error("Error al exportar Excel de reservas: {}", e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Apache POI", "No se pudo exportar el reporte Excel."));
        }
    }

    public List<ReservaDTO> getListaReservas() { return listaReservas; }
    public List<TipoHabitacionDTO> getListaTipos() { return listaTipos; }
    public List<HabitacionDTO> getHabitacionesDisponibles() { return habitacionesDisponibles; }
    public String getFiltroEstado() { return filtroEstado; }
    public void setFiltroEstado(String filtroEstado) { this.filtroEstado = filtroEstado; }
    public ReservaDTO getNuevaReserva() { return nuevaReserva; }
    public void setNuevaReserva(ReservaDTO nuevaReserva) { this.nuevaReserva = nuevaReserva; }
    public String getFechaCheckinStr() { return fechaCheckinStr; }
    public void setFechaCheckinStr(String fechaCheckinStr) { this.fechaCheckinStr = fechaCheckinStr; }
    public String getFechaCheckoutStr() { return fechaCheckoutStr; }
    public void setFechaCheckoutStr(String fechaCheckoutStr) { this.fechaCheckoutStr = fechaCheckoutStr; }
    public CotizacionDTO getCotizacionActual() { return cotizacionActual; }
    public int getTotalReservas() { return totalReservas; }
    public int getPendientes() { return pendientes; }
    public int getConfirmadas() { return confirmadas; }
    public int getEnCheckin() { return enCheckin; }
    public BigDecimal getIngresosTotales() { return ingresosTotales; }
}
