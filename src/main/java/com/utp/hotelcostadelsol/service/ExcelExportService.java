package com.utp.hotelcostadelsol.service;

import com.google.common.base.Preconditions;
import com.utp.hotelcostadelsol.model.dto.HabitacionDTO;
import com.utp.hotelcostadelsol.model.dto.ReservaDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio especializado en la generacion de reportes ejecutivos Microsoft Excel (.xlsx)
 * utilizando la libreria Apache POI (XSSFWorkbook), validaciones con Google Guava,
 * utilidades de Apache Commons y trazabilidad con Logback (SLF4J).
 */
public class ExcelExportService implements IReporteExportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelExportService.class);

    @Override
    public byte[] exportarInventarioHabitacionesExcel(List<HabitacionDTO> habitaciones) {
        Preconditions.checkNotNull(habitaciones, "La lista de habitaciones no puede ser nula");

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Inventario Habitaciones");
            CellStyle headerStyle = crearEstiloEncabezado(workbook);
            CellStyle dataStyle = crearEstiloCeldaDatos(workbook);
            CellStyle currencyStyle = crearEstiloMoneda(workbook);

            String[] headers = {"ID", "N° Habitación", "Piso", "Categoría / Tipo", "Estado Actual", "Precio x Noche (S/)", "Observaciones"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (HabitacionDTO h : CollectionUtils.emptyIfNull(habitaciones)) {
                Row row = sheet.createRow(rowIdx++);

                crearCelda(row, 0, String.valueOf(h.getIdHabitacion()), dataStyle);
                crearCelda(row, 1, StringUtils.defaultString(h.getNumero()), dataStyle);
                crearCelda(row, 2, "Piso " + h.getPiso(), dataStyle);
                crearCelda(row, 3, StringUtils.defaultString(h.getTipoNombre()), dataStyle);
                crearCelda(row, 4, StringUtils.defaultString(h.getEstado()), dataStyle);

                Cell priceCell = row.createCell(5);
                BigDecimal precio = h.getPrecioPorNoche() != null ? h.getPrecioPorNoche() : BigDecimal.ZERO;
                priceCell.setCellValue(precio.doubleValue());
                priceCell.setCellStyle(currencyStyle);

                crearCelda(row, 6, StringUtils.defaultIfBlank(h.getObservaciones(), "Sin observaciones"), dataStyle);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            LOGGER.info("Reporte Excel (.xlsx) de inventario generado con Apache POI ({} filas).", rowIdx - 1);
            return out.toByteArray();
        } catch (IOException e) {
            LOGGER.error("Error al generar archivo Excel de habitaciones con Apache POI: {}", e.getMessage());
            throw new RuntimeException("Error al exportar inventario a Excel", e);
        }
    }

    @Override
    public byte[] exportarReporteReservasExcel(List<ReservaDTO> reservas) {
        Preconditions.checkNotNull(reservas, "La lista de reservas no puede ser nula");

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Reporte de Reservas");
            CellStyle headerStyle = crearEstiloEncabezado(workbook);
            CellStyle dataStyle = crearEstiloCeldaDatos(workbook);
            CellStyle currencyStyle = crearEstiloMoneda(workbook);

            String[] headers = {"Código Reserva", "Huésped / Cliente", "Categoría", "Habitación", "Check-in", "Check-out", "Noches", "Estado", "Monto Total (S/)"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            BigDecimal sumaTotal = BigDecimal.ZERO;

            for (ReservaDTO r : CollectionUtils.emptyIfNull(reservas)) {
                Row row = sheet.createRow(rowIdx++);

                crearCelda(row, 0, StringUtils.defaultString(r.getCodigoReserva()), dataStyle);
                crearCelda(row, 1, StringUtils.defaultIfBlank(r.getNombreCliente(), "Huésped"), dataStyle);
                crearCelda(row, 2, StringUtils.defaultString(r.getNombreTipoHabitacion()), dataStyle);
                crearCelda(row, 3, StringUtils.defaultIfBlank(r.getNumeroHabitacion(), "Sin asignar"), dataStyle);
                crearCelda(row, 4, r.getFechaCheckin() != null ? r.getFechaCheckin().toString() : "-", dataStyle);
                crearCelda(row, 5, r.getFechaCheckout() != null ? r.getFechaCheckout().toString() : "-", dataStyle);
                crearCelda(row, 6, String.valueOf(r.getCantidadNoches()), dataStyle);
                crearCelda(row, 7, StringUtils.defaultString(r.getEstado()), dataStyle);

                Cell totalCell = row.createCell(8);
                BigDecimal monto = r.getMontoTotal() != null ? r.getMontoTotal() : BigDecimal.ZERO;
                totalCell.setCellValue(monto.doubleValue());
                totalCell.setCellStyle(currencyStyle);

                if (!"CANCELADA".equalsIgnoreCase(r.getEstado())) {
                    sumaTotal = sumaTotal.add(monto);
                }
            }

            // Fila de totalizacion financiera
            Row totalRow = sheet.createRow(rowIdx);
            crearCelda(totalRow, 7, "TOTAL ACTIVO:", headerStyle);
            Cell sumCell = totalRow.createCell(8);
            sumCell.setCellValue(sumaTotal.doubleValue());
            sumCell.setCellStyle(currencyStyle);

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            LOGGER.info("Reporte Excel (.xlsx) de reservas generado con Apache POI ({} reservas, Total S/ {}).", reservas.size(), sumaTotal);
            return out.toByteArray();
        } catch (IOException e) {
            LOGGER.error("Error al generar archivo Excel de reservas con Apache POI: {}", e.getMessage());
            throw new RuntimeException("Error al exportar reservas a Excel", e);
        }
    }

    private void crearCelda(Row row, int col, String valor, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor);
        cell.setCellStyle(style);
    }

    private CellStyle crearEstiloEncabezado(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private CellStyle crearEstiloCeldaDatos(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle crearEstiloMoneda(Workbook wb) {
        CellStyle style = crearEstiloCeldaDatos(wb);
        DataFormat format = wb.createDataFormat();
        style.setDataFormat(format.getFormat("\"S/ \"#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }
}
