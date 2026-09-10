package com.utp.hotelcostadelsol.service;

import com.utp.hotelcostadelsol.dao.UsuarioDAO;
import com.utp.hotelcostadelsol.model.dto.UsuarioDTO;

import java.util.Optional;

public class AuthService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public UsuarioDTO autenticar(String username, String password) {
        if (username == null || password == null) return null;
        Optional<UsuarioDTO> opt = usuarioDAO.validarCredenciales(username.trim(), password.trim());
        return opt.orElse(null);
    }
}