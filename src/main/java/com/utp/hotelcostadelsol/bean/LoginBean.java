package com.utp.hotelcostadelsol.bean;

import com.utp.hotelcostadelsol.model.dto.UsuarioDTO;
import com.utp.hotelcostadelsol.security.Rol;
import com.utp.hotelcostadelsol.service.AuthService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.Map;

/**
 * Managed Bean (@SessionScoped) encargado de la autenticacion de usuarios
 * y del control de permisos por rol (RBAC) en las vistas Facelets.
 */
@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String SESSION_USER_KEY = "usuarioLogueado";

    private String username;
    private String password;
    private UsuarioDTO usuarioLogueado;
    private boolean autenticado = false;

    private final AuthService authService = new AuthService();

    public String autenticar() {
        UsuarioDTO user = authService.autenticar(username, password);
        if (user != null) {
            this.usuarioLogueado = user;
            this.autenticado = true;

            FacesContext fc = FacesContext.getCurrentInstance();
            if (fc != null) {
                ExternalContext ec = fc.getExternalContext();
                Map<String, Object> sessionMap = ec.getSessionMap();
                sessionMap.put(SESSION_USER_KEY, user);
                fc.addMessage(null, new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "Bienvenido",
                        "Sesión iniciada como " + user.getNombreCompleto() + " (" + user.getRolNombre() + ")"
                ));
            }

            if (isEsAdmin()) {
                return "habitaciones.xhtml?faces-redirect=true";
            } else if (isEsRecepcion()) {
                return "recepcion.xhtml?faces-redirect=true";
            } else {
                return "reservas.xhtml?faces-redirect=true";
            }
        } else {
            FacesContext fc = FacesContext.getCurrentInstance();
            if (fc != null) {
                fc.addMessage(null, new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Error de Acceso",
                        "Usuario o contraseña incorrectos."
                ));
            }
            return null;
        }
    }

    public String cerrarSesion() {
        this.usuarioLogueado = null;
        this.autenticado = false;
        this.username = null;
        this.password = null;
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc != null) {
            fc.getExternalContext().getSessionMap().remove(SESSION_USER_KEY);
            fc.getExternalContext().invalidateSession();
        }
        return "index.xhtml?faces-redirect=true";
    }

    public boolean isEsAdmin() {
        if (!autenticado || usuarioLogueado == null) {
            return false;
        }
        return Rol.ROLE_ADMIN == Rol.fromCodigo(usuarioLogueado.getRolCodigo())
                || usuarioLogueado.getIdRol() == Rol.ROLE_ADMIN.getIdRol();
    }

    public boolean isEsRecepcion() {
        if (!autenticado || usuarioLogueado == null) {
            return false;
        }
        return Rol.ROLE_RECEPCION == Rol.fromCodigo(usuarioLogueado.getRolCodigo())
                || usuarioLogueado.getIdRol() == Rol.ROLE_RECEPCION.getIdRol();
    }

    public boolean isEsCliente() {
        if (!autenticado || usuarioLogueado == null) {
            return false;
        }
        return Rol.ROLE_CLIENTE == Rol.fromCodigo(usuarioLogueado.getRolCodigo())
                || usuarioLogueado.getIdRol() == Rol.ROLE_CLIENTE.getIdRol();
    }

    public boolean isEsPersonalOperativo() {
        return isEsAdmin() || isEsRecepcion();
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public UsuarioDTO getUsuarioLogueado() { return usuarioLogueado; }
    public boolean isAutenticado() { return autenticado; }
}
