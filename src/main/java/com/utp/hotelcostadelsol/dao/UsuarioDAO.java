package com.utp.hotelcostadelsol.dao;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.utp.hotelcostadelsol.config.DatabaseConfig;
import com.utp.hotelcostadelsol.model.dto.UsuarioDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UsuarioDAO {

    private static final Map<String, UsuarioDTO> MOCK_USERS = new HashMap<>();

    static {
        MOCK_USERS.put("admin", new UsuarioDTO(1, 1, "ROLE_ADMIN", "Administrador General", "admin", "Max Rivera Moran", "maxrivera938@gmail.com", "ACTIVO"));
        MOCK_USERS.put("recepcion", new UsuarioDTO(2, 2, "ROLE_RECEPCION", "Recepcionista", "recepcion", "Miguel Angel Suarez", "Miguelperdom543@gmail.com", "ACTIVO"));
        MOCK_USERS.put("cliente", new UsuarioDTO(3, 3, "ROLE_CLIENTE", "Cliente Huesped", "cliente", "Neo Ventura Lorenzo", "nventtura420@gmail.com", "ACTIVO"));
    }

    public Optional<UsuarioDTO> validarCredenciales(String username, String plainPassword) {
        if (!DatabaseConfig.isDbAvailable()) {
            if (MOCK_USERS.containsKey(username) && (plainPassword.contains("123") || plainPassword.equals("admin") || plainPassword.equals("admin123"))) {
                return Optional.of(MOCK_USERS.get(username));
            }
            return Optional.empty();
        }

        String sql = "SELECT u.id_usuario, u.id_rol, r.codigo AS rol_codigo, r.nombre AS rol_nombre, " +
                     "u.username, u.password_hash, u.nombre_completo, u.email, u.telefono, u.estado " +
                     "FROM usuarios u " +
                     "JOIN roles r ON u.id_rol = r.id_rol " +
                     "WHERE u.username = ? AND u.estado = 'ACTIVO'";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("password_hash");
                    BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), hash);
                    if (result.verified) {
                        UsuarioDTO u = new UsuarioDTO();
                        u.setIdUsuario(rs.getInt("id_usuario"));
                        u.setIdRol(rs.getInt("id_rol"));
                        u.setRolCodigo(rs.getString("rol_codigo"));
                        u.setRolNombre(rs.getString("rol_nombre"));
                        u.setUsername(rs.getString("username"));
                        u.setNombreCompleto(rs.getString("nombre_completo"));
                        u.setEmail(rs.getString("email"));
                        u.setTelefono(rs.getString("telefono"));
                        u.setEstado(rs.getString("estado"));
                        return Optional.of(u);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}