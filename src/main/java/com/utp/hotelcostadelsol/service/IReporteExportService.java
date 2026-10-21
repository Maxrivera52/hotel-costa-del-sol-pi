package com.utp.hotelcostadelsol.service;

import com.utp.hotelcostadelsol.model.dto.HabitacionDTO;
import com.utp.hotelcostadelsol.model.dto.ReservaDTO;
import java.util.List;

/**
 * Contrato SOLID (Interface Segregation & Dependency Inversion) para la exportacion
 * de reportes gerenciales e inventarios en formato Microsoft Excel (.xlsx).
 */
public interface IReporteExportService {

    /**
     * Genera un archivo Excel (.xlsx) en memoria con el inventario actual de habitaciones.
     *
     * @param habitaciones lista de habitaciones a exportar
     * @return arreglo de bytes del libro Excel
     */
    byte[] exportarInventarioHabitacionesExcel(List<HabitacionDTO> habitaciones);

    /**
     * Genera un archivo Excel (.xlsx) en memoria con el reporte consolidado de reservas e ingresos.
     *
     * @param reservas lista de reservas a exportar
     * @return arreglo de bytes del libro Excel
     */
    byte[] exportarReporteReservasExcel(List<ReservaDTO> reservas);
}
