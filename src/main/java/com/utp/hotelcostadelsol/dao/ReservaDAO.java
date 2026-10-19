package com.utp.hotelcostadelsol.dao;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.utp.hotelcostadelsol.config.DatabaseConfig;
import com.utp.hotelcostadelsol.model.dto.ReservaDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementacion DAO para la gestion de Reservas sobre PostgreSQL con fallback resiliente.
 * Aplica el contrato SOLID IReservaDAO, Google Guava, Apache Commons Lang3 y Logback.
 */
public class ReservaDAO implements IReservaDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservaDAO.class);
    private static final List<ReservaDTO> MOCK_RESERVAS = new ArrayList<>();

    static {
        MOCK_RESERVAS.add(new ReservaDTO(1, "WYN-2026-A102BC", 3, "Neo Ventura Lorenzo",
                1, "Habitacion Simple Confort", 2, "102",
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(1),
                1, new BigDecimal("240.00"), BigDecimal.ZERO, new BigDecimal("240.00"),
                "CHECK_IN", "WEB", "Viaje corporativo ejecutivo"));
        MOCK_RESERVAS.add(new ReservaDTO(2, "WYN-2026-D401EF", 3, "Neo Ventura Lorenzo",
                4, "Suite Junior Costa del Sol", 7, "401",
                LocalDate.now(), LocalDate.now().plusDays(3),
                2, new BigDecimal("960.00"), new BigDecimal("80.00"), new BigDecimal("1040.00"),
                "CHECK_IN", "RECEPCION", "Aniversario con vista panoramica"));
        MOCK_RESERVAS.add(new ReservaDTO(3, "WYN-2026-P201GH", 3, "Neo Ventura Lorenzo",
                2, "Habitacion Matrimonial Deluxe", 3, "201",
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(5),
                2, new BigDecimal("540.00"), BigDecimal.ZERO, new BigDecimal("540.00"),
                "CONFIRMADA", "WEB", "Llegada en vuelo nocturno"));
        MOCK_RESERVAS.add(new ReservaDTO(4, "WYN-2026-X501JK", 3, "Neo Ventura Lorenzo",
                5, "Suite Presidencial Real", null, "Sin asignar",
                LocalDate.now().plusDays(4), LocalDate.now().plusDays(6),
                3, new BigDecimal("1100.00"), new BigDecimal("120.00"), new BigDecimal("1220.00"),
                "PENDIENTE", "WEB", "Requiere recojo aeropuerto"));
    }

    @Override
    public List<ReservaDTO> findAll() {
        if (!DatabaseConfig.isDbAvailable()) {
            return ImmutableList.copyOf(MOCK_RESERVAS);
        }

        List<ReservaDTO> list = new ArrayList<>();
        String sql = "SELECT r.id_reserva, r.codigo_reserva, r.id_cliente, u.nombre_completo AS nombre_cliente, " +
                     "       r.id_tipo_habitacion, th.nombre AS nombre_tipo, r.id_habitacion, " +
                     "       COALESCE(h.numero, 'Sin asignar') AS numero_habitacion, " +
                     "       r.fecha_checkin, r.fecha_checkout, r.numero_huespedes, r.solicita_factura, " +
                     "       r.comentarios, r.precio_alojamiento, r.precio_servicios_extra, r.monto_total, " +
                     "       r.estado, r.origen, r.fecha_creacion " +
                     "FROM reservas r " +
                     "JOIN usuarios u ON r.id_cliente = u.id_usuario " +
                     "JOIN tipos_habitacion th ON r.id_tipo_habitacion = th.id_tipo " +
                     "LEFT JOIN habitaciones h ON r.id_habitacion = h.id_habitacion " +
                     "ORDER BY r.fecha_creacion DESC, r.id_reserva DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("Error al listar reservas desde PostgreSQL: {}", e.getMessage());
            return ImmutableList.copyOf(MOCK_RESERVAS);
        }
        return ImmutableList.copyOf(list);
    }

    @Override
    public Optional<ReservaDTO> findById(int idReserva) {
        return findAll().stream()
                .filter(r -> r.getIdReserva() != null && r.getIdReserva() == idReserva)
                .findFirst();
    }

    @Override
    public Optional<ReservaDTO> findByCodigo(String codigoReserva) {
        if (StringUtils.isBlank(codigoReserva)) {
            return Optional.empty();
        }
        String clean = StringUtils.trimToEmpty(codigoReserva);
        return findAll().stream()
                .filter(r -> StringUtils.equalsIgnoreCase(r.getCodigoReserva(), clean))
                .findFirst();
    }

    @Override
    public List<ReservaDTO> findByEstado(String estado) {
        if (StringUtils.isBlank(estado)) {
            return findAll();
        }
        String clean = StringUtils.trimToEmpty(estado);
        return findAll().stream()
                .filter(r -> StringUtils.equalsIgnoreCase(r.getEstado(), clean))
                .collect(Collectors.toList());
    }

    @Override
    public ReservaDTO save(ReservaDTO reserva) {
        Preconditions.checkNotNull(reserva, "ReservaDTO no puede ser nulo");
        Preconditions.checkArgument(StringUtils.isNotBlank(reserva.getCodigoReserva()), "Codigo de reserva obligatorio");

        if (!DatabaseConfig.isDbAvailable()) {
            int nextId = MOCK_RESERVAS.stream()
                    .mapToInt(r -> r.getIdReserva() != null ? r.getIdReserva() : 0)
                    .max().orElse(0) + 1;
            reserva.setIdReserva(nextId);
            if (StringUtils.isBlank(reserva.getNumeroHabitacion())) {
                reserva.setNumeroHabitacion("Sin asignar");
            }
            MOCK_RESERVAS.add(0, reserva);
            LOGGER.info("Reserva {} guardada en almacenamiento resiliente.", reserva.getCodigoReserva());
            return reserva;
        }

        String sql = "INSERT INTO reservas (codigo_reserva, id_cliente, id_tipo_habitacion, id_habitacion, " +
                     "fecha_checkin, fecha_checkout, numero_huespedes, solicita_factura, comentarios, " +
                     "precio_alojamiento, precio_servicios_extra, monto_total, estado, origen) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, reserva.getCodigoReserva());
            ps.setInt(2, reserva.getIdCliente() != null ? reserva.getIdCliente() : 3);
            ps.setInt(3, reserva.getIdTipoHabitacion());
            if (reserva.getIdHabitacion() != null && reserva.getIdHabitacion() > 0) {
                ps.setInt(4, reserva.getIdHabitacion());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setDate(5, Date.valueOf(reserva.getFechaCheckin()));
            ps.setDate(6, Date.valueOf(reserva.getFechaCheckout()));
            ps.setInt(7, reserva.getNumeroHuespedes() != null ? reserva.getNumeroHuespedes() : 1);
            ps.setBoolean(8, Boolean.TRUE.equals(reserva.getSolicitaFactura()));
            ps.setString(9, StringUtils.trimToEmpty(reserva.getComentarios()));
            ps.setBigDecimal(10, reserva.getPrecioAlojamiento());
            ps.setBigDecimal(11, reserva.getPrecioServiciosExtra() != null ? reserva.getPrecioServiciosExtra() : BigDecimal.ZERO);
            ps.setBigDecimal(12, reserva.getMontoTotal());
            ps.setString(13, StringUtils.defaultIfBlank(reserva.getEstado(), "PENDIENTE"));
            ps.setString(14, StringUtils.defaultIfBlank(reserva.getOrigen(), "WEB"));

            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) {
                    reserva.setIdReserva(gk.getInt(1));
                }
            }
            LOGGER.info("Reserva {} persistida en PostgreSQL con ID={}.", reserva.getCodigoReserva(), reserva.getIdReserva());
            return reserva;
        } catch (SQLException e) {
            LOGGER.error("Error al guardar reserva {}: {}", reserva.getCodigoReserva(), e.getMessage());
            throw new RuntimeException("Error al guardar la reserva en base de datos", e);
        }
    }

    @Override
    public boolean updateEstado(int idReserva, String nuevoEstado) {
        Preconditions.checkArgument(idReserva > 0, "ID de reserva invalido");
        Preconditions.checkArgument(StringUtils.isNotBlank(nuevoEstado), "El nuevo estado es obligatorio");

        if (!DatabaseConfig.isDbAvailable()) {
            for (ReservaDTO r : MOCK_RESERVAS) {
                if (r.getIdReserva() != null && r.getIdReserva() == idReserva) {
                    r.setEstado(nuevoEstado.trim().toUpperCase());
                    LOGGER.info("Reserva ID={} cambio a estado {} en memoria.", idReserva, nuevoEstado);
                    return true;
                }
            }
            return false;
        }

        String sql = "UPDATE reservas SET estado = ? WHERE id_reserva = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado.trim().toUpperCase());
            ps.setInt(2, idReserva);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Error al actualizar estado de reserva ID={}: {}", idReserva, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean assignHabitacion(int idReserva, int idHabitacion, String numeroHabitacion) {
        if (!DatabaseConfig.isDbAvailable()) {
            for (ReservaDTO r : MOCK_RESERVAS) {
                if (r.getIdReserva() != null && r.getIdReserva() == idReserva) {
                    r.setIdHabitacion(idHabitacion);
                    r.setNumeroHabitacion(StringUtils.defaultIfBlank(numeroHabitacion, String.valueOf(idHabitacion)));
                    return true;
                }
            }
            return false;
        }

        String sql = "UPDATE reservas SET id_habitacion = ? WHERE id_reserva = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idHabitacion);
            ps.setInt(2, idReserva);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Error al asignar habitacion {} a reserva {}: {}", idHabitacion, idReserva, e.getMessage());
            return false;
        }
    }

    private ReservaDTO mapRow(ResultSet rs) throws SQLException {
        ReservaDTO r = new ReservaDTO();
        r.setIdReserva(rs.getInt("id_reserva"));
        r.setCodigoReserva(rs.getString("codigo_reserva"));
        r.setIdCliente(rs.getInt("id_cliente"));
        r.setNombreCliente(rs.getString("nombre_cliente"));
        r.setIdTipoHabitacion(rs.getInt("id_tipo_habitacion"));
        r.setNombreTipoHabitacion(rs.getString("nombre_tipo"));
        int idHab = rs.getInt("id_habitacion");
        r.setIdHabitacion(rs.wasNull() ? null : idHab);
        r.setNumeroHabitacion(rs.getString("numero_habitacion"));

        Date ci = rs.getDate("fecha_checkin");
        if (ci != null) r.setFechaCheckin(ci.toLocalDate());
        Date co = rs.getDate("fecha_checkout");
        if (co != null) r.setFechaCheckout(co.toLocalDate());

        r.setNumeroHuespedes(rs.getInt("numero_huespedes"));
        r.setSolicitaFactura(rs.getBoolean("solicita_factura"));
        r.setComentarios(rs.getString("comentarios"));
        r.setPrecioAlojamiento(rs.getBigDecimal("precio_alojamiento"));
        r.setPrecioServiciosExtra(rs.getBigDecimal("precio_servicios_extra"));
        r.setMontoTotal(rs.getBigDecimal("monto_total"));
        r.setEstado(rs.getString("estado"));
        r.setOrigen(rs.getString("origen"));
        Timestamp ts = rs.getTimestamp("fecha_creacion");
        if (ts != null) r.setFechaCreacion(ts.toLocalDateTime());
        return r;
    }
}
