package com.utp.hotelcostadelsol.dao;

import com.utp.hotelcostadelsol.model.dto.PagoDTO;
import java.util.List;

/**
 * Contrato de acceso a datos (Principio SOLID de Inversión de Dependencias - DIP)
 * para el registro y verificación de pagos / comprobantes de reserva.
 */
public interface IPagoDAO {

    List<PagoDTO> findAll();

    PagoDTO save(PagoDTO pago);
}
