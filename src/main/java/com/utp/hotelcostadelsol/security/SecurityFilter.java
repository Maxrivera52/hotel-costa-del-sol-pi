package com.utp.hotelcostadelsol.security;

import com.google.common.collect.ImmutableSet;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

/**
 * Filtro de seguridad perimetral HTTP que inyecta cabeceras de proteccion OWASP
 * (anti-Clickjacking, XSS y MIME-sniffing) y registra accesos con Logback (SLF4J)
 * utilizando colecciones inmutables de Google Guava.
 */
@WebFilter(urlPatterns = "/*", dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD})
public class SecurityFilter extends HttpFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityFilter.class);

    private static final Set<String> STATIC_EXTENSIONS = ImmutableSet.of(
            ".css", ".js", ".jpg", ".jpeg", ".png", ".gif", ".ico", ".svg", ".woff", ".woff2"
    );

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Cabeceras de seguridad recomendadas (OWASP)
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        String uri = request.getRequestURI();
        if (uri != null && !isStaticResource(uri)) {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
            LOGGER.debug("Peticion HTTP segura procesada: [{}] {}", request.getMethod(), uri);
        }

        chain.doFilter(request, response);
    }

    private boolean isStaticResource(String uri) {
        String lower = uri.toLowerCase();
        if (lower.contains("/jakarta.faces.resource/") || lower.contains("/resources/")) {
            return true;
        }
        for (String ext : STATIC_EXTENSIONS) {
            if (lower.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}
