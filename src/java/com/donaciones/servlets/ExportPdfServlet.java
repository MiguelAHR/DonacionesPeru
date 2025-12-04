package com.donaciones.servlets;

import com.donaciones.utils.DataManager;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ExportPdfServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("=== INICIANDO EXPORTACIÓN PDF ===");
        
        // 1. VERIFICAR SESIÓN
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String username = (String) session.getAttribute("username");
        
        OutputStream out = null;
        Document document = null;
        
        try {
            // 2. CONFIGURAR RESPUESTA COMO PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", 
                "attachment; filename=\"Reporte_" + username + "_" + 
                new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".pdf\"");
            
            // 3. OBTENER DATOS
            DataManager dm = DataManager.getInstance();
            long totalDonations = dm.getEmployeeDonations(username);
            long activeDonations = dm.getEmployeeActiveDonations(username);
            long completedDonations = dm.getEmployeeCompletedDonations(username);
            Map<String, Long> donationsByType = dm.getEmployeeDonationsByType(username);
            
            // 4. CREAR DOCUMENTO PDF
            document = new Document();
            out = response.getOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();
            
            // 5. AGREGAR CONTENIDO AL PDF
            agregarContenidoPDF(document, username, totalDonations, activeDonations, 
                              completedDonations, donationsByType);
            
            document.close();
            
            System.out.println("✅ PDF generado exitosamente para: " + username);
            
        } catch (Exception e) {
            System.err.println("❌ ERROR generando PDF: " + e.getMessage());
            e.printStackTrace();
            
            if (document != null && document.isOpen()) {
                document.close();
            }
            
            // Mostrar error
            response.reset();
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<h3>Error generando PDF</h3>");
            response.getWriter().println("<p>" + e.getMessage() + "</p>");
            
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    private void agregarContenidoPDF(Document document, String username, 
                                    long total, long activas, long completadas,
                                    Map<String, Long> porTipo) throws DocumentException {
        
        // Fuentes
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        
        // Título
        Paragraph title = new Paragraph("REPORTE DE EMPLEADO - " + username.toUpperCase(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Fecha
        Paragraph fecha = new Paragraph(
            "Fecha de generación: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), 
            normalFont);
        fecha.setAlignment(Element.ALIGN_RIGHT);
        fecha.setSpacingAfter(15);
        document.add(fecha);
        
        // Línea separadora
        document.add(new Paragraph(" "));
        
        // Estadísticas
        Paragraph statsTitle = new Paragraph("ESTADÍSTICAS GENERALES", headerFont);
        statsTitle.setSpacingAfter(10);
        document.add(statsTitle);
        
        // Tabla de estadísticas
        PdfPTable statsTable = new PdfPTable(2);
        statsTable.setWidthPercentage(100);
        
        agregarCelda(statsTable, "Total de donaciones:", headerFont);
        agregarCelda(statsTable, String.valueOf(total), normalFont);
        
        agregarCelda(statsTable, "Donaciones activas:", headerFont);
        agregarCelda(statsTable, String.valueOf(activas), normalFont);
        
        agregarCelda(statsTable, "Donaciones completadas:", headerFont);
        agregarCelda(statsTable, String.valueOf(completadas), normalFont);
        
        if (total > 0) {
            double porcentaje = (completadas * 100.0) / total;
            agregarCelda(statsTable, "Tasa de completación:", headerFont);
            agregarCelda(statsTable, String.format("%.1f%%", porcentaje), normalFont);
        }
        
        document.add(statsTable);
        document.add(new Paragraph(" "));
        
        // Distribución por tipo
        if (porTipo != null && !porTipo.isEmpty()) {
            Paragraph typeTitle = new Paragraph("DISTRIBUCIÓN POR TIPO DE DONACIÓN", headerFont);
            typeTitle.setSpacingAfter(10);
            document.add(typeTitle);
            
            PdfPTable typeTable = new PdfPTable(2);
            typeTable.setWidthPercentage(100);
            
            agregarCelda(typeTable, "Tipo", headerFont);
            agregarCelda(typeTable, "Cantidad", headerFont);
            
            for (Map.Entry<String, Long> entry : porTipo.entrySet()) {
                agregarCelda(typeTable, entry.getKey(), normalFont);
                agregarCelda(typeTable, String.valueOf(entry.getValue()), normalFont);
            }
            
            document.add(typeTable);
        }
        
        // Pie de página
        document.add(new Paragraph(" "));
        Paragraph footer = new Paragraph(
            "Reporte generado automáticamente por el Sistema de Donaciones", 
            FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }
    
    private void agregarCelda(PdfPTable table, String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setPadding(5);
        table.addCell(cell);
    }
}