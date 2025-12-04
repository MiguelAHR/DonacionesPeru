package com.donaciones.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.OutputStream;
import java.util.Map;

public class PdfExporter {

    public void generateEmployeeReport(Map<String, Object> data, OutputStream outputStream) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();
        
        // Título
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph title = new Paragraph("REPORTE DE ACTIVIDAD DEL EMPLEADO", titleFont);
        document.add(title);
        
        document.add(new Paragraph(" "));
        
        // Información
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        
        document.add(new Paragraph("Empleado: " + data.get("employeeUsername"), normalFont));
        document.add(new Paragraph("Fecha: " + 
            new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()), normalFont));
        
        document.add(new Paragraph(" "));
        
        // Estadísticas
        document.add(new Paragraph("ESTADÍSTICAS:", boldFont));
        document.add(new Paragraph("Mis donaciones: " + data.get("myDonations"), normalFont));
        document.add(new Paragraph("Activas: " + data.get("activeDonations"), normalFont));
        document.add(new Paragraph("Completadas: " + data.get("completedDonations"), normalFont));
        
        document.add(new Paragraph(" "));
        
        // Donaciones por tipo
        document.add(new Paragraph("DONACIONES POR TIPO:", boldFont));
        
        Map<String, Long> donationsByType = (Map<String, Long>) data.get("donationsByType");
        if (donationsByType != null) {
            for (Map.Entry<String, Long> entry : donationsByType.entrySet()) {
                document.add(new Paragraph(entry.getKey() + ": " + entry.getValue(), normalFont));
            }
        }
        
        document.close();
    }
}