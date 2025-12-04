package test;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import java.io.*;

public class TestExcelFile {
    public static void main(String[] args) {
        System.out.println("=== PRUEBA DIRECTA POI ===");
        
        try {
            // 1. Crear archivo en disco C:/
            String filePath = "C:/test_excel_directo.xlsx";
            
            // 2. Crear workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Prueba");
            
            // 3. Escribir datos
            Row row1 = sheet.createRow(0);
            row1.createCell(0).setCellValue("Nombre");
            row1.createCell(1).setCellValue("Valor");
            
            Row row2 = sheet.createRow(1);
            row2.createCell(0).setCellValue("Donaciones");
            row2.createCell(1).setCellValue(100);
            
            Row row3 = sheet.createRow(2);
            row3.createCell(0).setCellValue("Activas");
            row3.createCell(2).setCellValue(50);
            
            // 4. Guardar
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
                System.out.println("✅ Archivo creado: " + filePath);
                
                // Verificar tamaño
                File file = new File(filePath);
                System.out.println("📏 Tamaño: " + file.length() + " bytes");
                
                if (file.length() < 3000) {
                    System.out.println("⚠️  ADVERTENCIA: Archivo muy pequeño, puede estar vacío");
                }
            }
            
            // 5. Cerrar
            workbook.close();
            
            // 6. Intentar leerlo para verificar
            System.out.println("\n🔍 Verificando archivo...");
            verifyExcelFile(filePath);
            
        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void verifyExcelFile(String filePath) {
        try {
            // Intentar abrir como ZIP (los .xlsx son ZIPs)
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", 
                "\"C:\\Program Files\\7-Zip\\7z.exe\" l \"" + filePath + "\"");
            Process p = pb.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            boolean hasContent = false;
            
            while ((line = reader.readLine()) != null) {
                if (line.contains("[Content_Types].xml") || line.contains("xl/")) {
                    hasContent = true;
                    System.out.println("✓ Estructura Excel encontrada: " + line);
                }
            }
            
            if (hasContent) {
                System.out.println("\n✅ El archivo TIENE estructura Excel válida");
            } else {
                System.out.println("\n❌ El archivo NO TIENE estructura Excel");
            }
            
        } catch (Exception e) {
            System.out.println("⚠️  No se pudo verificar con 7-Zip: " + e.getMessage());
        }
    }
}