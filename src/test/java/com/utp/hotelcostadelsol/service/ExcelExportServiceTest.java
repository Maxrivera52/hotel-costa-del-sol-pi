package com.utp.hotelcostadelsol.service;

import com.utp.hotelcostadelsol.model.dto.HabitacionDTO;
import com.utp.hotelcostadelsol.model.dto.ReservaDTO;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias TDD para ExcelExportService verificando la generacion de libros .xlsx con Apache POI.
 */
class ExcelExportServiceTest {

    private final IReporteExportService exportService = new ExcelExportService();

    @Test
    @DisplayName("Debe generar un libro Excel .xlsx valido para el inventario de habitaciones con Apache POI")
    void shouldGenerateRoomsInventoryExcelWorkbook() throws Exception {
        List<HabitacionDTO> habitaciones = List.of(
                new HabitacionDTO(1, 1, "101", 1, "Habitacion Simple Confort", "DISPONIBLE", new BigDecimal("120.00"), "Vista jardin"),
                new HabitacionDTO(2, 4, "401", 4, "Suite Junior Costa del Sol", "OCUPADA", new BigDecimal("320.00"), "Reserva VIP")
        );

        byte[] bytes = exportService.exportarInventarioHabitacionesExcel(habitaciones);

        assertNotNull(bytes);
        assertTrue(bytes.length > 0);

        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            Sheet sheet = wb.getSheet("Inventario Habitaciones");
            assertNotNull(sheet);
            assertEquals("N° Habitación", sheet.getRow(0).getCell(1).getStringCellValue());
            assertEquals("101", sheet.getRow(1).getCell(1).getStringCellValue());
            assertEquals(320.00, sheet.getRow(2).getCell(5).getNumericCellValue(), 0.01);
        }
    }

    @Test
    @DisplayName("Debe generar un libro Excel .xlsx valido para el reporte de reservas y totalizar ingresos")
    void shouldGenerateReservationsExcelWorkbook() throws Exception {
        List<ReservaDTO> reservas = List.of(
                new ReservaDTO(1, "WYN-2026-TEST01", 3, "Joel Cori", 2, "Matrimonial", 3, "201",
                        LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 22),
                        2, new BigDecimal("360.00"), BigDecimal.ZERO, new BigDecimal("360.00"),
                        "CONFIRMADA", "WEB", "")
        );

        byte[] bytes = exportService.exportarReporteReservasExcel(reservas);

        assertNotNull(bytes);
        assertTrue(bytes.length > 0);

        try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            Sheet sheet = wb.getSheet("Reporte de Reservas");
            assertNotNull(sheet);
            assertEquals("WYN-2026-TEST01", sheet.getRow(1).getCell(0).getStringCellValue());
            assertEquals("TOTAL ACTIVO:", sheet.getRow(2).getCell(7).getStringCellValue());
            assertEquals(360.00, sheet.getRow(2).getCell(8).getNumericCellValue(), 0.01);
        }
    }
}
