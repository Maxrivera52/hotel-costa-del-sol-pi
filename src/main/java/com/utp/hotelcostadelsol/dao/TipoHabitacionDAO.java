package com.utp.hotelcostadelsol.dao;

import com.utp.hotelcostadelsol.config.DatabaseConfig;
import com.utp.hotelcostadelsol.model.dto.TipoHabitacionDTO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TipoHabitacionDAO {

    private static final List<TipoHabitacionDTO> MOCK_TIPOS = new ArrayList<>();

    static {
        MOCK_TIPOS.add(new TipoHabitacionDTO(1, "simple", "Habitacion Simple Confort", "Ideal para viajes ejecutivos", new BigDecimal("120.00"), 1));
        MOCK_TIPOS.add(new TipoHabitacionDTO(2, "matrimonial", "Habitacion Matrimonial Deluxe", "Cama King Size y balcon", new BigDecimal("180.00"), 2));
        MOCK_TIPOS.add(new TipoHabitacionDTO(3, "doble", "Habitacion Doble Twin", "Dos camas Twin confortables", new BigDecimal("220.00"), 3));
        MOCK_TIPOS.add(new TipoHabitacionDTO(4, "suite_jr", "Suite Junior Costa del Sol", "Sala de estar e hidromasaje", new BigDecimal("320.00"), 2));
        MOCK_TIPOS.add(new TipoHabitacionDTO(5, "suite_pres", "Suite Presidencial Real", "Terraza panoramica y acabados VIP", new BigDecimal("550.00"), 4));
    }

    public List<TipoHabitacionDTO> findAll() {
        if (!DatabaseConfig.isDbAvailable()) {
            return new ArrayList<>(MOCK_TIPOS);
        }

        List<TipoHabitacionDTO> list = new ArrayList<>();
        String sql = "SELECT id_tipo, codigo, nombre, descripcion, precio_base_noche, capacidad_maxima FROM tipos_habitacion WHERE activo = TRUE ORDER BY id_tipo";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new TipoHabitacionDTO(
                    rs.getInt("id_tipo"),
                    rs.getString("codigo"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getBigDecimal("precio_base_noche"),
                    rs.getInt("capacidad_maxima")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>(MOCK_TIPOS);
        }
        return list;
    }
}