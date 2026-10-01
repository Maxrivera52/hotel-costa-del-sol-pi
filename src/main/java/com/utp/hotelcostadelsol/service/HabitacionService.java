package com.utp.hotelcostadelsol.service;

import com.utp.hotelcostadelsol.dao.HabitacionDAO;
import com.utp.hotelcostadelsol.dao.TipoHabitacionDAO;
import com.utp.hotelcostadelsol.model.dto.HabitacionDTO;
import com.utp.hotelcostadelsol.model.dto.TipoHabitacionDTO;

import java.util.List;
import java.util.Map;

public class HabitacionService {

    private final HabitacionDAO habitacionDAO = new HabitacionDAO();
    private final TipoHabitacionDAO tipoHabitacionDAO = new TipoHabitacionDAO();

    public List<HabitacionDTO> listarTodas() {
        return habitacionDAO.findAll();
    }

    public List<HabitacionDTO> listarPorFiltro(String estado, Integer idTipo) {
        return habitacionDAO.findByFiltro(estado, idTipo);
    }

    public List<TipoHabitacionDTO> listarTipos() {
        return tipoHabitacionDAO.findAll();
    }

    public boolean registrarHabitacion(HabitacionDTO h) {
        if (h.getNumero() == null || h.getNumero().trim().isEmpty()) {
            return false;
        }
        return habitacionDAO.save(h);
    }

    public boolean actualizarEstado(int idHabitacion, String nuevoEstado) {
        return habitacionDAO.updateEstado(idHabitacion, nuevoEstado);
    }

    public Map<String, Integer> obtenerResumenEstados() {
        return habitacionDAO.contarPorEstado();
    }
}