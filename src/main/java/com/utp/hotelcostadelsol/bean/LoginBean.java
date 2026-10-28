package com.utp.hotelcostadelsol.bean;

import com.utp.hotelcostadelsol.model.dto.UsuarioDTO;
import com.utp.hotelcostadelsol.service.AuthService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {
    private static final long serialVersionUID = 1L;

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
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenido", "Sesión iniciada como " + user.getNombreCompleto()));
            return "habitaciones.xhtml?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error de Acceso", "Usuario o contraseña incorrectos."));
            return null;
        }
    }

    public String cerrarSesion() {
        this.usuarioLogueado = null;
        this.autenticado = false;
        this.username = null;
        this.password = null;
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "login.xhtml?faces-redirect=true";
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public UsuarioDTO getUsuarioLogueado() { return usuarioLogueado; }
    public boolean isAutenticado() { return autenticado; }
}