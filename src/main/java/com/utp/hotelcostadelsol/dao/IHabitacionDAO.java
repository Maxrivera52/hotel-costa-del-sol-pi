package com.utp.hotelcostadelsol.dao;

import com.utp.hotelcostadelsol.model.dto.HabitacionDTO;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Contrato de acceso a datos (Principio SOLID de Inversion de Dependencias - DIP)
 * para la gestion de inventario de habitaciones.
 */
public interface IHabitacionDAO {

    List<HabitacionDTO> findAll();

    Optional<HabitacionDTO> findById(int idHabitacion);

    List<HabitacionDTO> findByFiltro(String estado, Integer idTipo);

    boolean save(HabitacionDTO habitacion);

    boolean updateEstado(int idHabitacion, String nuevoEstado);

    Map<String, Integer> contarPorEstado();
}
