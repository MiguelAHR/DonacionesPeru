package com.donaciones.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.OutputStream;
import java.util.Map;

public class ExcelExporter {

    public void generateEmployeeReport(Map<String, Object> data, OutputStream outputStream) throws Exception {
        System.out.println("[ExcelExporter] Iniciando...");
        
        Workbook workbook = null;
        try {
            // 1. CREAR WORKBOOK
            workbook = new XSSFWorkbook();
            
            // 2. CREAR HOJA
            Sheet sheet = workbook.createSheet("Reporte Empleado");
            
            // 3. ESTILOS BÁSICOS
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            
            CellStyle labelStyle = workbook.createCellStyle();
            Font labelFont = workbook.createFont();
            labelFont.setBold(true);
            labelStyle.setFont(labelFont);
            
            // 4. DATOS
            int rowNum = 0;
            
            // TÍTULO
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("REPORTE DE ACTIVIDAD - EMPLEADO");
            titleCell.setCellStyle(headerStyle);
            
            rowNum++; // Espacio
            
            // INFORMACIÓN DEL EMPLEADO
            Row empRow = sheet.createRow(rowNum++);
            empRow.createCell(0).setCellValue("Empleado:");
            empRow.createCell(1).setCellValue(data.getOrDefault("employeeUsername", "N/A").toString());
            
            Row dateRow = sheet.createRow(rowNum++);
            dateRow.createCell(0).setCellValue("Fecha Generación:");
            dateRow.createCell(1).setCellValue(new java.util.Date().toString());
            
            rowNum++; // Espacio
            
            // ESTADÍSTICAS - TÍTULO
            Row statsTitleRow = sheet.createRow(rowNum++);
            statsTitleRow.createCell(0).setCellValue("ESTADÍSTICAS GENERALES");
            statsTitleRow.getCell(0).setCellStyle(labelStyle);
            
            // ESTADÍSTICAS - DATOS
            Row totalRow = sheet.createRow(rowNum++);
            totalRow.createCell(0).setCellValue("Total Donaciones:");
            totalRow.createCell(1).setCellValue(data.getOrDefault("myDonations", 0).toString());
            
            Row activeRow = sheet.createRow(rowNum++);
            activeRow.createCell(0).setCellValue("Donaciones Activas:");
            activeRow.createCell(1).setCellValue(data.getOrDefault("activeDonations", 0).toString());
            
            Row completedRow = sheet.createRow(rowNum++);
            completedRow.createCell(0).setCellValue("Donaciones Completadas:");
            completedRow.createCell(1).setCellValue(data.getOrDefault("completedDonations", 0).toString());
            
            // DONACIONES POR TIPO
            Object donationsByTypeObj = data.get("donationsByType");
            if (donationsByTypeObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Long> donationsByType = (Map<String, Long>) donationsByTypeObj;
                
                if (donationsByType != null && !donationsByType.isEmpty()) {
                    rowNum++; // Espacio
                    
                    Row typeTitleRow = sheet.createRow(rowNum++);
                    typeTitleRow.createCell(0).setCellValue("DONACIONES POR TIPO");
                    typeTitleRow.getCell(0).setCellStyle(labelStyle);
                    
                    for (Map.Entry<String, Long> entry : donationsByType.entrySet()) {
                        Row typeRow = sheet.createRow(rowNum++);
                        typeRow.createCell(0).setCellValue(entry.getKey());
                        typeRow.createCell(1).setCellValue(entry.getValue() != null ? entry.getValue().doubleValue() : 0.0);
                    }
                }
            }
            
            // 5. AJUSTAR COLUMNAS
            for (int i = 0; i < 2; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // 6. ESCRIBIR
            System.out.println("[ExcelExporter] Escribiendo Excel...");
            workbook.write(outputStream);
            
            System.out.println("[ExcelExporter] ✅ Excel generado correctamente");
            
        } finally {
            // 7. CERRAR WORKBOOK
            if (workbook != null) {
                try {
                    workbook.close();
                    System.out.println("[ExcelExporter] Workbook cerrado");
                } catch (Exception e) {
                    System.err.println("[ExcelExporter] Error cerrando workbook: " + e.getMessage());
                }
            }
        }
    }
}