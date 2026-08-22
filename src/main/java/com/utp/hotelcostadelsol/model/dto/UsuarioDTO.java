package com.utp.hotelcostadelsol.model.dto;

import java.io.Serializable;

public class UsuarioDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idUsuario;
    private int idRol;
    private String rolCodigo;
    private String rolNombre;
    private String username;
    private String passwordHash;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private String estado;

    public UsuarioDTO() {}

    public UsuarioDTO(int idUsuario, int idRol, String rolCodigo, String rolNombre, String username, String nombreCompleto, String email, String estado) {
        this.idUsuario = idUsuario;
        this.idRol = idRol;
        this.rolCodigo = rolCodigo;
        this.rolNombre = rolNombre;
        this.username = username;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.estado = estado;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }
    public String getRolCodigo() { return rolCodigo; }
    public void setRolCodigo(String rolCodigo) { this.rolCodigo = rolCodigo; }
    public String getRolNombre() { return rolNombre; }
    public void setRolNombre(String rolNombre) { this.rolNombre = rolNombre; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}