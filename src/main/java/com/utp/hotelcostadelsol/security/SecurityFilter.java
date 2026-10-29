package com.utp.hotelcostadelsol.security;

import com.google.common.collect.ImmutableSet;
import com.utp.hotelcostadelsol.bean.LoginBean;
import com.utp.hotelcostadelsol.model.dto.UsuarioDTO;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

/**
 * Filtro de seguridad perimetral HTTP que inyecta cabeceras de proteccion OWASP,
 * aplica el control de acceso basado en roles (RBAC: ROLE_ADMIN, ROLE_RECEPCION, ROLE_CLIENTE)
 * y registra accesos con Logback (SLF4J) utilizando colecciones inmutables de Google Guava.
 */
@WebFilter(urlPatterns = "/*", dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD})
public class SecurityFilter extends HttpFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityFilter.class);

    private static final Set<String> STATIC_EXTENSIONS = ImmutableSet.of(
            ".css", ".js", ".jpg", ".jpeg", ".png", ".gif", ".ico", ".svg", ".woff", ".woff2", ".mp4", ".mp3"
    );

    private static final Set<String> STATIC_PREFIXES = ImmutableSet.of(
            "/jakarta.faces.resource/", "/resources/", "/image/", "/styles/", "/scripts/", "/video/", "/audio/"
    );

    private static final Set<String> PUBLIC_VIEWS = ImmutableSet.of(
            "/", "/index.xhtml", "/index.html", "/servicios.xhtml", "/acerca.xhtml",
            "/contacto.xhtml", "/login.xhtml", "/acceso-denegado.xhtml"
    );

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 1. Cabeceras de seguridad recomendadas (OWASP)
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        String path = extractPath(request);
        if (isStaticResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        LOGGER.debug("Peticion HTTP segura procesada: [{}] {}", request.getMethod(), path);

        // 2. Permitir vistas publicas del portal hotelero
        if (isPublicView(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 3. Verificar autenticacion para vistas protegidas (habitaciones, reservas, recepcion)
        UsuarioDTO loggedUser = getLoggedUser(request);
        String contextPath = request.getContextPath() != null ? request.getContextPath() : "";

        if (loggedUser == null) {
            LOGGER.warn("Intento de acceso no autenticado a ruta protegida: {}", path);
            response.sendRedirect(contextPath + "/login.xhtml");
            return;
        }

        // 4. Control de Acceso Basado en Roles (RBAC)
        Rol userRol = resolveRol(loggedUser);
        if (isRecepcionOrAdminOnlyPath(path)) {
            if (userRol != Rol.ROLE_ADMIN && userRol != Rol.ROLE_RECEPCION) {
                LOGGER.warn("Acceso denegado RBAC para usuario '{}' ({}) en ruta: {}",
                        loggedUser.getUsername(), userRol, path);
                response.sendRedirect(contextPath + "/acceso-denegado.xhtml");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private String extractPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri == null) {
            return "/";
        }
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)) {
            uri = uri.substring(contextPath.length());
        }
        return uri.isEmpty() ? "/" : uri;
    }

    private boolean isStaticResource(String path) {
        if (path == null) {
            return false;
        }
        String lower = path.toLowerCase();
        for (String prefix : STATIC_PREFIXES) {
            if (lower.startsWith(prefix) || lower.contains(prefix)) {
                return true;
            }
        }
        for (String ext : STATIC_EXTENSIONS) {
            if (lower.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPublicView(String path) {
        if (path == null || path.isEmpty()) {
            return true;
        }
        return PUBLIC_VIEWS.contains(path);
    }

    private boolean isRecepcionOrAdminOnlyPath(String path) {
        return "/recepcion.xhtml".equalsIgnoreCase(path) || path.startsWith("/recepcion");
    }

    private UsuarioDTO getLoggedUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object attr = session.getAttribute(LoginBean.SESSION_USER_KEY);
        if (attr instanceof UsuarioDTO user) {
            return user;
        }
        return null;
    }

    private Rol resolveRol(UsuarioDTO user) {
        Rol byCode = Rol.fromCodigo(user.getRolCodigo());
        if (byCode != null) {
            return byCode;
        }
        if (user.getIdRol() == Rol.ROLE_ADMIN.getIdRol()) {
            return Rol.ROLE_ADMIN;
        }
        if (user.getIdRol() == Rol.ROLE_RECEPCION.getIdRol()) {
            return Rol.ROLE_RECEPCION;
        }
        return Rol.ROLE_CLIENTE;
    }
}
