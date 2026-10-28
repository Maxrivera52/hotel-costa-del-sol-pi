package com.utp.hotelcostadelsol.dao;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.utp.hotelcostadelsol.config.DatabaseConfig;
import com.utp.hotelcostadelsol.model.dto.PagoDTO;
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

/**
 * DAO para el registro y verificacion de pagos y vouchers de reservas.
 */
public class PagoDAO implements IPagoDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(PagoDAO.class);
    private static final List<PagoDTO> MOCK_PAGOS = new ArrayList<>();

    static {
        MOCK_PAGOS.add(new PagoDTO(1, 1, "WYN-2026-A102BC", new BigDecimal("240.00"), "TARJETA_CREDITO", "OP-908123", "VOUCHER-102.pdf", "VERIFICADO", 2, LocalDateTime.now().minusDays(1)));
        MOCK_PAGOS.add(new PagoDTO(2, 2, "WYN-2026-D401EF", new BigDecimal("1040.00"), "TRANSFERENCIA_BANCARIA", "BCP-445512", "VOUCHER-401.pdf", "VERIFICADO", 2, LocalDateTime.now().minusHours(5)));
        MOCK_PAGOS.add(new PagoDTO(3, 3, "WYN-2026-P201GH", new BigDecimal("540.00"), "YAPE_PLIN", "YP-778899", "VOUCHER-201.png", "VERIFICADO", 2, LocalDateTime.now().minusHours(2)));
    }

    public List<PagoDTO> findAll() {
        if (!DatabaseConfig.isDbAvailable()) {
            return ImmutableList.copyOf(MOCK_PAGOS);
        }

        List<PagoDTO> list = new ArrayList<>();
        String sql = "SELECT p.id_pago, p.id_reserva, r.codigo_reserva, p.monto, p.metodo_pago, " +
                     "       p.numero_operacion, p.comprobante_voucher, p.estado_pago, p.id_recepcionista, p.fecha_pago " +
                     "FROM pagos p " +
                     "JOIN reservas r ON p.id_reserva = r.id_reserva " +
                     "ORDER BY p.fecha_pago DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("fecha_pago");
                list.add(new PagoDTO(
                        rs.getInt("id_pago"),
                        rs.getInt("id_reserva"),
                        rs.getString("codigo_reserva"),
                        rs.getBigDecimal("monto"),
                        rs.getString("metodo_pago"),
                        rs.getString("numero_operacion"),
                        rs.getString("comprobante_voucher"),
                        rs.getString("estado_pago"),
                        rs.getInt("id_recepcionista"),
                        ts != null ? ts.toLocalDateTime() : LocalDateTime.now()
                ));
            }
        } catch (SQLException e) {
            LOGGER.error("Error al listar pagos desde PostgreSQL: {}", e.getMessage());
            return ImmutableList.copyOf(MOCK_PAGOS);
        }
        return ImmutableList.copyOf(list);
    }

    public PagoDTO save(PagoDTO pago) {
        Preconditions.checkNotNull(pago, "PagoDTO no puede ser nulo");
        Preconditions.checkArgument(pago.getMonto() != null && pago.getMonto().compareTo(BigDecimal.ZERO) > 0, "El monto debe ser mayor a cero");

        if (!DatabaseConfig.isDbAvailable()) {
            pago.setIdPago(MOCK_PAGOS.size() + 1);
            MOCK_PAGOS.add(0, pago);
            LOGGER.info("Pago registrado en memoria por S/ {} para reserva ID={}", pago.getMonto(), pago.getIdReserva());
            return pago;
        }

        String sql = "INSERT INTO pagos (id_reserva, monto, metodo_pago, numero_operacion, comprobante_voucher, estado_pago, id_recepcionista, fecha_pago) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, pago.getIdReserva());
            ps.setBigDecimal(2, pago.getMonto());
            ps.setString(3, StringUtils.defaultIfBlank(pago.getMetodoPago(), "EFECTIVO_VENTANILLA"));
            ps.setString(4, StringUtils.trimToEmpty(pago.getNumeroOperacion()));
            ps.setString(5, StringUtils.trimToEmpty(pago.getComprobanteVoucher()));
            ps.setString(6, StringUtils.defaultIfBlank(pago.getEstadoPago(), "VERIFICADO"));
            ps.setInt(7, pago.getIdRecepcionista() != null ? pago.getIdRecepcionista() : 2);
            ps.setTimestamp(8, Timestamp.valueOf(pago.getFechaPago() != null ? pago.getFechaPago() : LocalDateTime.now()));
            ps.executeUpdate();
            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) {
                    pago.setIdPago(gk.getInt(1));
                }
            }
            return pago;
        } catch (SQLException e) {
            LOGGER.error("Error al guardar pago en PostgreSQL: {}", e.getMessage());
            throw new RuntimeException("Error al guardar pago", e);
        }
    }
}
