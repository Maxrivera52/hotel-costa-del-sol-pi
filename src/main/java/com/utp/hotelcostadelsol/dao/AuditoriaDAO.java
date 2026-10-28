package com.utp.hotelcostadelsol.dao;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.utp.hotelcostadelsol.config.DatabaseConfig;
import com.utp.hotelcostadelsol.model.dto.AuditoriaDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * DAO para el registro y consulta de la bitacora de auditoria de seguridad.
 * Aplica Logback (SLF4J), Google Guava (Preconditions, ImmutableList) y Apache Commons Lang3.
 */
public class AuditoriaDAO implements IAuditoriaDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditoriaDAO.class);
    private static final List<AuditoriaDTO> MOCK_AUDITORIA = new ArrayList<>();

    static {
        MOCK_AUDITORIA.add(new AuditoriaDTO(1, 1, "admin", "SEGURIDAD", "INIT_SYSTEM",
                "Inicialización de arquitectura MVC, DAO, SOLID y componentes del Proyecto Integrador", "127.0.0.1", LocalDateTime.now().minusHours(4)));
        MOCK_AUDITORIA.add(new AuditoriaDTO(2, 2, "recepcion", "RECEPCION", "CHECK_IN",
                "Check-in registrado para reserva WYN-2026-A102BC en habitacion 102", "192.168.1.15", LocalDateTime.now().minusHours(2)));
        MOCK_AUDITORIA.add(new AuditoriaDTO(3, 2, "recepcion", "RECEPCION", "VERIFICAR_PAGO",
                "Pago verificado por S/ 1040.00 para reserva WYN-2026-D401EF", "192.168.1.15", LocalDateTime.now().minusMinutes(45)));
    }

    /**
     * Registra un evento en la bitacora de auditoria y en el log de seguridad Logback.
     *
     * @param auditoria DTO con los datos del evento
     */
    public void registrarEvento(AuditoriaDTO auditoria) {
        Preconditions.checkNotNull(auditoria, "El objeto AuditoriaDTO no puede ser nulo");
        Preconditions.checkArgument(StringUtils.isNotBlank(auditoria.getModulo()), "El modulo es obligatorio");
        Preconditions.checkArgument(StringUtils.isNotBlank(auditoria.getAccion()), "La accion es obligatoria");

        LOGGER.info("[AUDITORIA-{}] Accion={} | Usuario={} | Detalle={}",
                auditoria.getModulo(), auditoria.getAccion(),
                StringUtils.defaultIfBlank(auditoria.getUsername(), String.valueOf(auditoria.getIdUsuario())),
                auditoria.getDescripcion());

        if (!DatabaseConfig.isDbAvailable()) {
            auditoria.setIdAuditoria(MOCK_AUDITORIA.size() + 1);
            if (StringUtils.isBlank(auditoria.getUsername())) {
                auditoria.setUsername(auditoria.getIdUsuario() != null && auditoria.getIdUsuario() == 1 ? "admin" : "recepcion");
            }
            MOCK_AUDITORIA.add(0, auditoria);
            return;
        }

        String sql = "INSERT INTO auditoria (id_usuario, modulo, accion, descripcion, ip_address, fecha_hora) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (auditoria.getIdUsuario() != null && auditoria.getIdUsuario() > 0) {
                ps.setInt(1, auditoria.getIdUsuario());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, StringUtils.trimToEmpty(auditoria.getModulo()));
            ps.setString(3, StringUtils.trimToEmpty(auditoria.getAccion()));
            ps.setString(4, StringUtils.trimToEmpty(auditoria.getDescripcion()));
            ps.setString(5, StringUtils.defaultIfBlank(auditoria.getIpAddress(), "127.0.0.1"));
            ps.setTimestamp(6, Timestamp.valueOf(auditoria.getFechaHora() != null ? auditoria.getFechaHora() : LocalDateTime.now()));
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Error al persistir evento de auditoria en PostgreSQL: {}", e.getMessage());
            MOCK_AUDITORIA.add(0, auditoria);
        }
    }

    /**
     * Obtiene los eventos de auditoria mas recientes como lista inmutable de Google Guava.
     *
     * @param limit cantidad maxima de registros
     * @return lista inmutable de AuditoriaDTO
     */
    public List<AuditoriaDTO> listarRecientes(int limit) {
        int max = limit > 0 ? limit : 25;
        if (!DatabaseConfig.isDbAvailable()) {
            return MOCK_AUDITORIA.stream()
                    .sorted(Comparator.comparing(AuditoriaDTO::getFechaHora, Comparator.nullsLast(Comparator.reverseOrder())))
                    .limit(max)
                    .collect(ImmutableList.toImmutableList());
        }

        List<AuditoriaDTO> list = new ArrayList<>();
        String sql = "SELECT a.id_auditoria, a.id_usuario, COALESCE(u.username, 'sistema') AS username, " +
                     "a.modulo, a.accion, a.descripcion, a.ip_address, a.fecha_hora " +
                     "FROM auditoria a LEFT JOIN usuarios u ON a.id_usuario = u.id_usuario " +
                     "ORDER BY a.fecha_hora DESC, a.id_auditoria DESC LIMIT ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, max);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("fecha_hora");
                    list.add(new AuditoriaDTO(
                            rs.getInt("id_auditoria"),
                            rs.getInt("id_usuario"),
                            rs.getString("username"),
                            rs.getString("modulo"),
                            rs.getString("accion"),
                            rs.getString("descripcion"),
                            rs.getString("ip_address"),
                            ts != null ? ts.toLocalDateTime() : LocalDateTime.now()
                    ));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error al consultar bitacora de auditoria: {}", e.getMessage());
            return ImmutableList.copyOf(MOCK_AUDITORIA);
        }
        return ImmutableList.copyOf(list);
    }
}
