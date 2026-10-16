package com.utp.hotelcostadelsol.security;

import org.apache.commons.lang3.StringUtils;

/**
 * Enumeracion de roles de seguridad RBAC (Role-Based Access Control)
 * para el sistema Hotel Wyndham Costa del Sol.
 */
public enum Rol {

    ROLE_ADMIN(1, "ROLE_ADMIN", "Administrador General"),
    ROLE_RECEPCION(2, "ROLE_RECEPCION", "Recepcionista"),
    ROLE_CLIENTE(3, "ROLE_CLIENTE", "Cliente Huesped");

    private final int idRol;
    private final String codigo;
    private final String nombre;

    Rol(int idRol, String codigo, String nombre) {
        this.idRol = idRol;
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public int getIdRol() {
        return idRol;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    /**
     * Resuelve un rol a partir de su codigo textual usando Apache Commons Lang3.
     *
     * @param codigo codigo del rol (ej. ROLE_ADMIN)
     * @return instancia de Rol o null si no coincide
     */
    public static Rol fromCodigo(String codigo) {
        if (StringUtils.isBlank(codigo)) {
            return null;
        }
        String normalized = StringUtils.trimToEmpty(codigo);
        for (Rol rol : values()) {
            if (StringUtils.equalsIgnoreCase(rol.codigo, normalized)) {
                return rol;
            }
        }
        return null;
    }
}
