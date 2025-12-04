package test;

import java.io.FileOutputStream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class TestPoiMinimal {
    public static void main(String[] args) {
        System.out.println("=== PRUEBA POI MÍNIMA ===");
        
        try {
            // 1. Crear workbook
            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("Prueba");
            
            // 2. Agregar una celda simple
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue("Hola Mundo POI");
            
            // 3. Guardar
            try (FileOutputStream fos = new FileOutputStream("C:/temp/test_minimal.xlsx")) {
                wb.write(fos);
                System.out.println("✅ Archivo creado: C:/temp/test_minimal.xlsx");
                System.out.println("   Tamaño: " + new java.io.File("C:/temp/test_minimal.xlsx").length() + " bytes");
            }
            
            wb.close();
            
        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}