package com.utp.hotelcostadelsol.model.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO que representa un registro en la bitacora de auditoria de seguridad y operaciones.
 */
public class AuditoriaDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private int idAuditoria;
    private Integer idUsuario;
    private String username;
    private String modulo;
    private String accion;
    private String descripcion;
    private String ipAddress;
    private LocalDateTime fechaHora;

    public AuditoriaDTO() {
        this.fechaHora = LocalDateTime.now();
        this.ipAddress = "127.0.0.1";
    }

    public AuditoriaDTO(int idAuditoria, Integer idUsuario, String username, String modulo,
                        String accion, String descripcion, String ipAddress, LocalDateTime fechaHora) {
        this.idAuditoria = idAuditoria;
        this.idUsuario = idUsuario;
        this.username = username;
        this.modulo = modulo;
        this.accion = accion;
        this.descripcion = descripcion;
        this.ipAddress = ipAddress;
        this.fechaHora = fechaHora != null ? fechaHora : LocalDateTime.now();
    }

    public String getFechaHoraFormateada() {
        return fechaHora != null ? fechaHora.format(FORMATTER) : "";
    }

    public int getIdAuditoria() { return idAuditoria; }
    public void setIdAuditoria(int idAuditoria) { this.idAuditoria = idAuditoria; }
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
}
