package com.utp.hotelcostadelsol.dao;

import com.utp.hotelcostadelsol.config.DatabaseConfig;
import com.utp.hotelcostadelsol.model.dto.HabitacionDTO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HabitacionDAO {

    private static final List<HabitacionDTO> MOCK_HABITACIONES = new ArrayList<>();

    static {
        MOCK_HABITACIONES.add(new HabitacionDTO(1, 1, "101", 1, "Habitacion Simple Confort", "DISPONIBLE", new BigDecimal("120.00"), "Vista al jardin interior"));
        MOCK_HABITACIONES.add(new HabitacionDTO(2, 1, "102", 1, "Habitacion Simple Confort", "OCUPADA", new BigDecimal("120.00"), "Check-out programado para hoy"));
        MOCK_HABITACIONES.add(new HabitacionDTO(3, 2, "201", 2, "Habitacion Matrimonial Deluxe", "DISPONIBLE", new BigDecimal("180.00"), "Cama King y balcon este"));
        MOCK_HABITACIONES.add(new HabitacionDTO(4, 2, "202", 2, "Habitacion Matrimonial Deluxe", "MANTENIMIENTO", new BigDecimal("180.00"), "Mantenimiento aire acondicionado"));
        MOCK_HABITACIONES.add(new HabitacionDTO(5, 3, "203", 2, "Habitacion Doble Twin", "DISPONIBLE", new BigDecimal("220.00"), "Dos camas Twin"));
        MOCK_HABITACIONES.add(new HabitacionDTO(6, 3, "301", 3, "Habitacion Doble Twin", "DISPONIBLE", new BigDecimal("220.00"), "Piso silencioso familiar"));
        MOCK_HABITACIONES.add(new HabitacionDTO(7, 4, "401", 4, "Suite Junior Costa del Sol", "OCUPADA", new BigDecimal("320.00"), "Reserva corporativa"));
        MOCK_HABITACIONES.add(new HabitacionDTO(8, 4, "402", 4, "Suite Junior Costa del Sol", "DISPONIBLE", new BigDecimal("320.00"), "Equipada con hidromasaje"));
        MOCK_HABITACIONES.add(new HabitacionDTO(9, 5, "501", 5, "Suite Presidencial Real", "DISPONIBLE", new BigDecimal("550.00"), "Terraza exclusiva VIP"));
    }

    public List<HabitacionDTO> findAll() {
        if (!DatabaseConfig.isDbAvailable()) {
            return new ArrayList<>(MOCK_HABITACIONES);
        }

        List<HabitacionDTO> list = new ArrayList<>();
        String sql = "SELECT h.id_habitacion, h.id_tipo, h.numero, h.piso, t.nombre AS tipo_nombre, " +
                     "h.estado, h.precio_por_noche, h.observaciones " +
                     "FROM habitaciones h " +
                     "JOIN tipos_habitacion t ON h.id_tipo = t.id_tipo " +
                     "ORDER BY h.piso ASC, h.numero ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new HabitacionDTO(
                    rs.getInt("id_habitacion"),
                    rs.getInt("id_tipo"),
                    rs.getString("numero"),
                    rs.getInt("piso"),
                    rs.getString("tipo_nombre"),
                    rs.getString("estado"),
                    rs.getBigDecimal("precio_por_noche"),
                    rs.getString("observaciones")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>(MOCK_HABITACIONES);
        }
        return list;
    }

    public List<HabitacionDTO> findByFiltro(String estado, Integer idTipo) {
        List<HabitacionDTO> todas = findAll();
        List<HabitacionDTO> filtradas = new ArrayList<>();

        for (HabitacionDTO h : todas) {
            boolean matchEstado = (estado == null || estado.trim().isEmpty() || h.getEstado().equalsIgnoreCase(estado));
            boolean matchTipo = (idTipo == null || idTipo <= 0 || h.getIdTipo() == idTipo);

            if (matchEstado && matchTipo) {
                filtradas.add(h);
            }
        }
        return filtradas;
    }

    /* save() implementado en APF2 */ private boolean _stub_save(HabitacionDTO h) {
        if (!DatabaseConfig.isDbAvailable()) {
            h.setIdHabitacion(MOCK_HABITACIONES.size() + 1);
            MOCK_HABITACIONES.add(h);
            return true;
        }

        String sql = "INSERT INTO habitaciones (id_tipo, numero, piso, estado, precio_por_noche, observaciones) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, h.getIdTipo());
            ps.setString(2, h.getNumero());
            ps.setInt(3, h.getPiso());
            ps.setString(4, h.getEstado());
            ps.setBigDecimal(5, h.getPrecioPorNoche());
            ps.setString(6, h.getObservaciones());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEstado(int idHabitacion, String nuevoEstado) {
        if (!DatabaseConfig.isDbAvailable()) {
            for (HabitacionDTO h : MOCK_HABITACIONES) {
                if (h.getIdHabitacion() == idHabitacion) {
                    h.setEstado(nuevoEstado);
                    return true;
                }
            }
            return false;
        }

        String sql = "UPDATE habitaciones SET estado = ? WHERE id_habitacion = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idHabitacion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Integer> contarPorEstado() {
        Map<String, Integer> map = new HashMap<>();
        int disponibles = 0;
        int ocupadas = 0;
        int mantenimiento = 0;

        List<HabitacionDTO> list = findAll();
        for (HabitacionDTO h : list) {
            if ("DISPONIBLE".equalsIgnoreCase(h.getEstado())) disponibles++;
            else if ("OCUPADA".equalsIgnoreCase(h.getEstado())) ocupadas++;
            else if ("MANTENIMIENTO".equalsIgnoreCase(h.getEstado())) mantenimiento++;
        }

        map.put("TOTAL", list.size());
        map.put("DISPONIBLE", disponibles);
        map.put("OCUPADA", ocupadas);
        map.put("MANTENIMIENTO", mantenimiento);
        return map;
    }
}