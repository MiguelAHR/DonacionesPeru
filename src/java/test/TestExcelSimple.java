package test;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;

public class TestExcelSimple {
    public static void main(String[] args) {
        System.out.println("=== PRUEBA APACHE POI ===");
        
        try {
            // Prueba 1: Crear archivo en disco
            System.out.println("\n1. Probando crear archivo en disco...");
            testFileOnDisk();
            
            // Prueba 2: Crear en memoria
            System.out.println("\n2. Probando crear en memoria (como en servlet)...");
            testInMemory();
            
            System.out.println("\n✅ ¡Todas las pruebas pasaron!");
            
        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testFileOnDisk() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Prueba");
        
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Prueba Apache POI");
        
        row = sheet.createRow(1);
        row.createCell(0).setCellValue("Fecha:");
        row.createCell(1).setCellValue(new java.util.Date().toString());
        
        // Guardar en disco
        try (FileOutputStream fos = new FileOutputStream("test_excel_simple.xlsx")) {
            workbook.write(fos);
        }
        
        workbook.close();
        System.out.println("   ✅ Archivo creado: test_excel_simple.xlsx");
    }
    
    private static void testInMemory() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Memoria");
        
        for (int i = 0; i < 5; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue("Fila " + (i + 1));
            row.createCell(1).setCellValue(i * 10);
        }
        
        // Escribir a ByteArrayOutputStream (como en servlet)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        
        byte[] excelData = baos.toByteArray();
        System.out.println("   ✅ Datos en memoria: " + excelData.length + " bytes");
        
        // Verificar que no esté vacío
        if (excelData.length > 100) {
            System.out.println("   ✅ Datos válidos (más de 100 bytes)");
        } else {
            System.out.println("   ⚠️  Advertencia: Datos muy pequeños (" + excelData.length + " bytes)");
        }
    }
}