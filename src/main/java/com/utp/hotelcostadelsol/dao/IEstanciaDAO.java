package com.utp.hotelcostadelsol.dao;

import com.utp.hotelcostadelsol.model.dto.EstanciaDTO;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos (Principio SOLID de Inversión de Dependencias - DIP)
 * para las estancias presenciales (Check-in y Check-out).
 */
public interface IEstanciaDAO {

    List<EstanciaDTO> findAll();

    Optional<EstanciaDTO> findByReserva(int idReserva);

    EstanciaDTO save(EstanciaDTO estancia);

    boolean registrarCheckout(int idReserva, BigDecimal consumosAdicionales, String observaciones, int idRecepcionista);
}
