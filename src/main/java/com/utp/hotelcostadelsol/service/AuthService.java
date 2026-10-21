package com.utp.hotelcostadelsol.service;

import com.utp.hotelcostadelsol.dao.AuditoriaDAO;
import com.utp.hotelcostadelsol.dao.UsuarioDAO;
import com.utp.hotelcostadelsol.model.dto.AuditoriaDTO;
import com.utp.hotelcostadelsol.model.dto.UsuarioDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Servicio de negocio para la autenticacion de usuarios y auditoria de accesos.
 * Aplica inyeccion de dependencias por constructor (SOLID), Apache Commons Lang3 y Logback.
 */
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    private final UsuarioDAO usuarioDAO;
    private final AuditoriaDAO auditoriaDAO;

    public AuthService() {
        this(new UsuarioDAO(), new AuditoriaDAO());
    }

    public AuthService(UsuarioDAO usuarioDAO, AuditoriaDAO auditoriaDAO) {
        this.usuarioDAO = usuarioDAO;
        this.auditoriaDAO = auditoriaDAO;
    }

    /**
     * Valida las credenciales de un usuario y registra el acceso en la bitacora de auditoria.
     *
     * @param username nombre de usuario
     * @param password contraseña en texto plano
     * @return UsuarioDTO autenticado o null si las credenciales son invalidas
     */
    public UsuarioDTO autenticar(String username, String password) {
        if (StringUtils.isAnyBlank(username, password)) {
            LOGGER.warn("Intento de autenticacion con credenciales vacias.");
            return null;
        }

        Optional<UsuarioDTO> opt = usuarioDAO.validarCredenciales(
                StringUtils.trimToEmpty(username),
                StringUtils.trimToEmpty(password)
        );

        if (opt.isPresent()) {
            UsuarioDTO u = opt.get();
            AuditoriaDTO audit = new AuditoriaDTO();
            audit.setIdUsuario(u.getIdUsuario());
            audit.setUsername(u.getUsername());
            audit.setModulo("SEGURIDAD");
            audit.setAccion("LOGIN_EXITOSO");
            audit.setDescripcion("Inicio de sesion exitoso para " + u.getNombreCompleto() + " (" + u.getRolCodigo() + ")");
            auditoriaDAO.registrarEvento(audit);
            return u;
        }
        return null;
    }
}
