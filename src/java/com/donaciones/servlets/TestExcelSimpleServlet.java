package com.donaciones.servlets;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

// Usa anotación para evitar web.xml
@WebServlet("/testExcelSimple")
public class TestExcelSimpleServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("=== TEST SERVLET: Iniciando ===");
        
        // DESACTIVAR CUALQUIER CACHÉ O BUFFER
        response.resetBuffer();
        response.reset();
        
        OutputStream out = null;
        Workbook workbook = null;
        
        try {
            // 1. LOG INICIAL
            System.out.println("Paso 1: Configurando respuesta...");
            
            // 2. CONFIGURAR CABECERAS PRIMERO
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"test_simple.xlsx\"");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            
            // 3. CREAR WORKBOOK (MUY SIMPLE)
            System.out.println("Paso 2: Creando workbook...");
            workbook = new XSSFWorkbook();
            System.out.println("✅ Workbook creado");
            
            // 4. CREAR HOJA Y DATOS
            Sheet sheet = workbook.createSheet("Prueba");
            System.out.println("✅ Sheet creado");
            
            // Fila 1
            Row row1 = sheet.createRow(0);
            Cell cell1 = row1.createCell(0);
            cell1.setCellValue("REPORTE DE PRUEBA");
            System.out.println("✅ Celda 1 creada");
            
            // Fila 2
            Row row2 = sheet.createRow(1);
            row2.createCell(0).setCellValue("Fecha:");
            row2.createCell(1).setCellValue(new java.util.Date().toString());
            System.out.println("✅ Celda 2 creada");
            
            // Fila 3
            Row row3 = sheet.createRow(2);
            row3.createCell(0).setCellValue("Usuario:");
            row3.createCell(1).setCellValue("test_user");
            System.out.println("✅ Celda 3 creada");
            
            // Fila 4 con número
            Row row4 = sheet.createRow(3);
            row4.createCell(0).setCellValue("Valor:");
            row4.createCell(1).setCellValue(100.50);
            System.out.println("✅ Celda 4 creada");
            
            // 5. OBTENER OutputStream
            System.out.println("Paso 3: Obteniendo OutputStream...");
            out = response.getOutputStream();
            System.out.println("✅ OutputStream obtenido");
            
            // 6. ESCRIBIR WORKBOOK
            System.out.println("Paso 4: Escribiendo workbook...");
            workbook.write(out);
            System.out.println("✅ Workbook escrito");
            
            // 7. FORZAR FLUSH
            System.out.println("Paso 5: Haciendo flush...");
            out.flush();
            System.out.println("✅ Flush completado");
            
            System.out.println("=== TEST SERVLET: ÉXITO COMPLETO ===");
            
        } catch (Exception e) {
            System.err.println("=== TEST SERVLET: ERROR ===");
            System.err.println("Tipo: " + e.getClass().getName());
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            
            // Enviar error como texto para debug
            try {
                response.reset();
                response.setContentType("text/plain;charset=UTF-8");
                PrintWriter writer = response.getWriter();
                writer.println("ERROR EN TEST SERVLET:");
                writer.println("Tipo: " + e.getClass().getName());
                writer.println("Mensaje: " + e.getMessage());
                writer.println("\nStackTrace:");
                e.printStackTrace(writer);
            } catch (Exception ex) {
                System.err.println("Error adicional: " + ex.getMessage());
            }
            
        } finally {
            // 8. CERRAR RECURSOS EN ORDEN INVERSO
            System.out.println("Paso 6: Cerrando recursos...");
            
            if (workbook != null) {
                try {
                    workbook.close();
                    System.out.println("✅ Workbook cerrado");
                } catch (Exception e) {
                    System.err.println("❌ Error cerrando workbook: " + e.getMessage());
                }
            }
            
            if (out != null) {
                try {
                    out.close();
                    System.out.println("✅ OutputStream cerrado");
                } catch (Exception e) {
                    System.err.println("❌ Error cerrando OutputStream: " + e.getMessage());
                }
            }
            
            System.out.println("=== TEST SERVLET: FINALIZADO ===");
        }
    }
}