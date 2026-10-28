package com.utp.hotelcostadelsol.dao;

import com.utp.hotelcostadelsol.model.dto.TipoHabitacionDTO;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos (Principio SOLID de Inversión de Dependencias - DIP)
 * para el catálogo de categorías de habitación.
 */
public interface ITipoHabitacionDAO {

    List<TipoHabitacionDTO> findAll();

    Optional<TipoHabitacionDTO> findById(int idTipo);
}
