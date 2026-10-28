package com.utp.hotelcostadelsol.dao;

import com.utp.hotelcostadelsol.model.dto.AuditoriaDTO;
import java.util.List;

/**
 * Contrato de acceso a datos (Principio SOLID de Inversión de Dependencias - DIP)
 * para la bitácora de auditoría y seguridad.
 */
public interface IAuditoriaDAO {

    void registrarEvento(AuditoriaDTO auditoria);

    List<AuditoriaDTO> listarRecientes(int limit);
}
