package com.utp.hotelcostadelsol.dao;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.utp.hotelcostadelsol.config.DatabaseConfig;
import com.utp.hotelcostadelsol.model.dto.EstanciaDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO para la gestion de estancias presenciales (Check-in y Check-out).
 */
public class EstanciaDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(EstanciaDAO.class);
    private static final List<EstanciaDTO> MOCK_ESTANCIAS = new ArrayList<>();

    static {
        MOCK_ESTANCIAS.add(new EstanciaDTO(1, 1, "WYN-2026-A102BC", "Neo Ventura Lorenzo", "102",
                LocalDateTime.now().minusDays(1), null, "KEY-RFID-102A", new BigDecimal("35.00"), "Huesped instalado sin novedades"));
        MOCK_ESTANCIAS.add(new EstanciaDTO(2, 2, "WYN-2026-D401EF", "Neo Ventura Lorenzo", "401",
                LocalDateTime.now().minusHours(5), null, "KEY-RFID-401B", new BigDecimal("80.00"), "Amenidades VIP entregadas en Suite Junior"));
    }

    public List<EstanciaDTO> findAll() {
        if (!DatabaseConfig.isDbAvailable()) {
            return ImmutableList.copyOf(MOCK_ESTANCIAS);
        }

        List<EstanciaDTO> list = new ArrayList<>();
        String sql = "SELECT e.id_estancia, e.id_reserva, r.codigo_reserva, u.nombre_completo AS nombre_huesped, " +
                     "       COALESCE(h.numero, '-') AS numero_habitacion, e.fecha_hora_checkin, e.fecha_hora_checkout, " +
                     "       e.llave_asignada, e.consumos_adicionales, e.observaciones " +
                     "FROM estancias e " +
                     "JOIN reservas r ON e.id_reserva = r.id_reserva " +
                     "JOIN usuarios u ON r.id_cliente = u.id_usuario " +
                     "LEFT JOIN habitaciones h ON r.id_habitacion = h.id_habitacion " +
                     "ORDER BY e.fecha_hora_checkin DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Timestamp ci = rs.getTimestamp("fecha_hora_checkin");
                Timestamp co = rs.getTimestamp("fecha_hora_checkout");
                list.add(new EstanciaDTO(
                        rs.getInt("id_estancia"),
                        rs.getInt("id_reserva"),
                        rs.getString("codigo_reserva"),
                        rs.getString("nombre_huesped"),
                        rs.getString("numero_habitacion"),
                        ci != null ? ci.toLocalDateTime() : LocalDateTime.now(),
                        co != null ? co.toLocalDateTime() : null,
                        rs.getString("llave_asignada"),
                        rs.getBigDecimal("consumos_adicionales"),
                        rs.getString("observaciones")
                ));
            }
        } catch (SQLException e) {
            LOGGER.error("Error al listar estancias desde PostgreSQL: {}", e.getMessage());
            return ImmutableList.copyOf(MOCK_ESTANCIAS);
        }
        return ImmutableList.copyOf(list);
    }

    public Optional<EstanciaDTO> findByReserva(int idReserva) {
        return findAll().stream()
                .filter(e -> e.getIdReserva() != null && e.getIdReserva() == idReserva)
                .findFirst();
    }

    public EstanciaDTO save(EstanciaDTO estancia) {
        Preconditions.checkNotNull(estancia, "EstanciaDTO no puede ser nulo");
        Preconditions.checkArgument(StringUtils.isNotBlank(estancia.getLlaveAsignada()), "La llave asignada es obligatoria");

        if (!DatabaseConfig.isDbAvailable()) {
            estancia.setIdEstancia(MOCK_ESTANCIAS.size() + 1);
            MOCK_ESTANCIAS.add(0, estancia);
            LOGGER.info("Check-in registrado en memoria para reserva ID={} con llave={}", estancia.getIdReserva(), estancia.getLlaveAsignada());
            return estancia;
        }

        String sql = "INSERT INTO estancias (id_reserva, fecha_hora_checkin, llave_asignada, consumos_adicionales, observaciones, id_recepcionista_checkin) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, estancia.getIdReserva());
            ps.setTimestamp(2, Timestamp.valueOf(estancia.getFechaHoraCheckin() != null ? estancia.getFechaHoraCheckin() : LocalDateTime.now()));
            ps.setString(3, StringUtils.trimToEmpty(estancia.getLlaveAsignada()));
            ps.setBigDecimal(4, estancia.getConsumosAdicionales() != null ? estancia.getConsumosAdicionales() : BigDecimal.ZERO);
            ps.setString(5, StringUtils.trimToEmpty(estancia.getObservaciones()));
            ps.setInt(6, estancia.getIdRecepcionistaCheckin() != null ? estancia.getIdRecepcionistaCheckin() : 2);
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) {
                    estancia.setIdEstancia(gk.getInt(1));
                }
            }
            return estancia;
        } catch (SQLException e) {
            LOGGER.error("Error al registrar estancia en PostgreSQL: {}", e.getMessage());
            throw new RuntimeException("Error al registrar check-in", e);
        }
    }

    public boolean registrarCheckout(int idReserva, BigDecimal consumosAdicionales, String observaciones, int idRecepcionista) {
        if (!DatabaseConfig.isDbAvailable()) {
            for (EstanciaDTO e : MOCK_ESTANCIAS) {
                if (e.getIdReserva() != null && e.getIdReserva() == idReserva) {
                    e.setFechaHoraCheckout(LocalDateTime.now());
                    e.setConsumosAdicionales(consumosAdicionales != null ? consumosAdicionales : BigDecimal.ZERO);
                    e.setObservaciones(StringUtils.defaultIfBlank(observaciones, e.getObservaciones()));
                    e.setIdRecepcionistaCheckout(idRecepcionista);
                    LOGGER.info("Check-out registrado en memoria para reserva ID={}", idReserva);
                    return true;
                }
            }
            return false;
        }

        String sql = "UPDATE estancias SET fecha_hora_checkout = ?, consumos_adicionales = ?, observaciones = ?, id_recepcionista_checkout = ? " +
                     "WHERE id_reserva = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setBigDecimal(2, consumosAdicionales != null ? consumosAdicionales : BigDecimal.ZERO);
            ps.setString(3, StringUtils.trimToEmpty(observaciones));
            ps.setInt(4, idRecepcionista);
            ps.setInt(5, idReserva);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Error al registrar checkout para reserva ID={}: {}", idReserva, e.getMessage());
            return false;
        }
    }
}
