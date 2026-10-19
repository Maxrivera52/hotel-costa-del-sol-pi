package com.utp.hotelcostadelsol.dao;

import com.utp.hotelcostadelsol.model.dto.ReservaDTO;
import java.util.List;
import java.util.Optional;

/**
 * Contrato DAO (SOLID - Dependency Inversion Principle) para la persistencia de reservas.
 */
public interface IReservaDAO {

    List<ReservaDTO> findAll();

    Optional<ReservaDTO> findById(int idReserva);

    Optional<ReservaDTO> findByCodigo(String codigoReserva);

    List<ReservaDTO> findByEstado(String estado);

    ReservaDTO save(ReservaDTO reserva);

    boolean updateEstado(int idReserva, String nuevoEstado);

    boolean assignHabitacion(int idReserva, int idHabitacion, String numeroHabitacion);
}
