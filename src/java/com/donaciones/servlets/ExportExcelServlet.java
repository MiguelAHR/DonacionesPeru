package com.donaciones.servlets;

import com.donaciones.utils.DataManager;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

public class ExportExcelServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("=== INICIANDO EXPORTACIÓN CSV CORREGIDO ===");
        
        // 1. VERIFICAR SESIÓN
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String username = (String) session.getAttribute("username");
        
        OutputStream outputStream = null;
        PrintWriter writer = null;
        
        try {
            // 2. CONFIGURACIÓN MEJORADA PARA EXCEL
            response.setContentType("text/csv; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", 
                "attachment; filename=\"Reporte_" + safeFileName(username) + "_" + 
                new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".csv\"");
            
            // 3. OBTENER OutputStream y crear Writer con UTF-8
            outputStream = response.getOutputStream();
            
            // Escribir BOM UTF-8 manualmente
            outputStream.write(0xEF);
            outputStream.write(0xBB);
            outputStream.write(0xBF);
            
            // Crear Writer con UTF-8 explícito
            writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            
            // 4. OBTENER DATOS
            DataManager dm = DataManager.getInstance();
            
            Long totalDonations = safeGetLong(dm.getEmployeeDonations(username));
            Long activeDonations = safeGetLong(dm.getEmployeeActiveDonations(username));
            Long completedDonations = safeGetLong(dm.getEmployeeCompletedDonations(username));
            Map<String, Long> donationsByType = dm.getEmployeeDonationsByType(username);
            Map<String, Long> donationsByLocation = dm.getEmployeeDonationsByLocation(username);
            
            // 5. GENERAR CSV SIMPLE Y CORRECTO (sin emojis problemáticos)
            generarCSVSimpleYCorrecto(writer, username, totalDonations, 
                                    activeDonations, completedDonations, 
                                    donationsByType, donationsByLocation);
            
            writer.flush();
            outputStream.flush();
            
            System.out.println("✅ CSV corregido generado para: " + username);
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            
            // Generar CSV de error simple
            try {
                response.reset();
                response.setContentType("text/csv; charset=UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=\"Error_Reporte.csv\"");
                writer = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));
                writer.println("sep=;");
                writer.println("ERROR AL GENERAR REPORTE;");
                writer.println("Mensaje;" + escapeCSV(e.getMessage(), ";"));
                writer.println("Fecha;" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            } catch (Exception ex) {
                // Ignorar
            }
            
        } finally {
            if (writer != null) writer.close();
            if (outputStream != null) outputStream.close();
        }
    }
    
    private Long safeGetLong(Long value) {
        return value != null ? value : 0L;
    }
    
    private String safeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9_]", "_");
    }
    
    private void generarCSVSimpleYCorrecto(PrintWriter writer, String username,
                                          Long total, Long activas, Long completadas,
                                          Map<String, Long> porTipo, Map<String, Long> porUbicacion) {
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String separator = ";"; // Punto y coma para Excel en español
        
        // 1. ESPECIFICAR SEPARADOR PARA EXCEL
        writer.println("sep=" + separator);
        writer.println();
        
        // 2. CABECERA DEL REPORTE
        writer.println("REPORTE DE ACTIVIDAD - " + username.toUpperCase());
        writer.println("Fecha de generacion" + separator + sdf.format(new Date()));
        writer.println("Sistema" + separator + "Donaciones Peru v2.0");
        writer.println();
        
        // 3. RESUMEN EJECUTIVO (sin emojis, solo texto)
        writer.println("=== RESUMEN EJECUTIVO ===");
        writer.println("Concepto" + separator + "Valor" + separator + "Porcentaje" + separator + "Estado" + separator + "Indicador");
        
        // Total donaciones
        String indicadorTotal = total >= 20 ? "EXCELENTE" : (total >= 10 ? "ACEPTABLE" : "MEJORAR");
        String estadoTotal = total >= 20 ? "SUPERADO" : (total >= 10 ? "OK" : "BAJO");
        writer.println("Total Donaciones" + separator + total + separator + "100%" + separator + estadoTotal + separator + indicadorTotal);
        
        if (total > 0) {
            double porcActivas = (activas.doubleValue() / total.doubleValue()) * 100;
            String indicadorActivas = activas > 0 ? "EN PROCESO" : "SIN ACTIVAS";
            writer.println("Donaciones Activas" + separator + activas + separator + 
                          String.format("%.1f", porcActivas) + "%" + separator + 
                          indicadorActivas + separator + "PENDIENTE");
            
            double porcCompletadas = (completadas.doubleValue() / total.doubleValue()) * 100;
            String indicadorCompletadas = completadas > 0 ? "FINALIZADO" : "SIN COMPLETADAS";
            writer.println("Donaciones Completadas" + separator + completadas + separator + 
                          String.format("%.1f", porcCompletadas) + "%" + separator + 
                          indicadorCompletadas + separator + "COMPLETADO");
            
            // Tasa de éxito
            String indicadorExito = porcCompletadas >= 75 ? "ALTA" : 
                                   (porcCompletadas >= 50 ? "MEDIA" : "BAJA");
            writer.println("Tasa de Exito" + separator + "" + separator + 
                          String.format("%.1f", porcCompletadas) + "%" + separator + 
                          indicadorExito + separator + "EFICIENCIA");
        }
        writer.println();
        
        // 4. DISTRIBUCIÓN POR TIPO
        writer.println("=== DISTRIBUCION POR TIPO ===");
        writer.println("Tipo de Donacion" + separator + "Cantidad" + separator + 
                      "Porcentaje" + separator + "Prioridad");
        
        if (porTipo != null && !porTipo.isEmpty()) {
            for (Map.Entry<String, Long> entry : porTipo.entrySet()) {
                double porcentaje = total > 0 ? (entry.getValue().doubleValue() / total.doubleValue()) * 100 : 0;
                String prioridad = porcentaje > 30 ? "ALTA" : 
                                  (porcentaje > 15 ? "MEDIA" : "BAJA");
                
                writer.println(escapeCSV(entry.getKey(), separator) + separator + 
                              entry.getValue() + separator + 
                              String.format("%.1f", porcentaje) + "%" + separator + 
                              prioridad);
            }
        } else {
            writer.println("Sin datos registrados" + separator + "0" + separator + 
                          "0%" + separator + "N/A");
        }
        writer.println();
        
        // 5. DISTRIBUCIÓN POR UBICACIÓN
        if (porUbicacion != null && !porUbicacion.isEmpty()) {
            writer.println("=== DISTRIBUCION POR UBICACION ===");
            writer.println("Ubicacion" + separator + "Cantidad" + separator + 
                          "Porcentaje" + separator + "Zona");
            
            Long totalUbicaciones = porUbicacion.values().stream().mapToLong(Long::longValue).sum();
            
            for (Map.Entry<String, Long> entry : porUbicacion.entrySet()) {
                double porcentaje = totalUbicaciones > 0 ? 
                    (entry.getValue().doubleValue() / totalUbicaciones.doubleValue()) * 100 : 0;
                String zona = "Zona " + (Math.abs(entry.getKey().hashCode()) % 5 + 1);
                
                writer.println(escapeCSV(entry.getKey(), separator) + separator + 
                              entry.getValue() + separator + 
                              String.format("%.1f", porcentaje) + "%" + separator + 
                              zona);
            }
            writer.println();
        }
        
        // 6. ESTADÍSTICAS AVANZADAS
        writer.println("=== ESTADISTICAS AVANZADAS ===");
        writer.println("Metrica" + separator + "Valor" + separator + 
                      "Objetivo" + separator + "Diferencia" + separator + "Cumplimiento");
        
        if (total > 0) {
            // Promedio diario
            double promedioDia = total / 30.0;
            double objetivoPromedio = 1.0;
            double difPromedio = promedioDia - objetivoPromedio;
            String cumplPromedio = promedioDia >= objetivoPromedio ? "SUPERADO" : "POR DEBAJO";
            writer.println("Promedio Diario" + separator + 
                          String.format("%.2f", promedioDia) + separator + 
                          objetivoPromedio + separator + 
                          String.format("%.2f", difPromedio) + separator + 
                          cumplPromedio);
            
            // Eficiencia
            double eficiencia = (completadas.doubleValue() / total.doubleValue()) * 100;
            double objetivoEficiencia = 70.0;
            double difEficiencia = eficiencia - objetivoEficiencia;
            String cumplEficiencia = eficiencia >= objetivoEficiencia ? "SUPERADA" : "POR DEBAJO";
            writer.println("Eficiencia Operativa" + separator + 
                          String.format("%.1f", eficiencia) + "%" + separator + 
                          objetivoEficiencia + "%" + separator + 
                          String.format("%.1f", difEficiencia) + "%" + separator + 
                          cumplEficiencia);
            
            // Ratio Activas/Completadas
            if (completadas > 0) {
                double ratio = activas.doubleValue() / completadas.doubleValue();
                double objetivoRatio = 0.5;
                double difRatio = ratio - objetivoRatio;
                String cumplRatio = ratio <= objetivoRatio ? "OPTIMO" : "ALTO";
                writer.println("Ratio Activas/Completadas" + separator + 
                              String.format("%.2f", ratio) + separator + 
                              objetivoRatio + separator + 
                              String.format("%.2f", difRatio) + separator + 
                              cumplRatio);
            }
        }
        writer.println();
        
        // 7. RECOMENDACIONES (sin checkbox Unicode)
        writer.println("=== RECOMENDACIONES DEL SISTEMA ===");
        writer.println("Prioridad" + separator + "Recomendacion" + separator + 
                      "Accion Sugerida" + separator + "Plazo" + separator + "Seguimiento");
        
        // Recomendaciones basadas en datos
        if (total < 5) {
            writer.println("ALTA" + separator + 
                          "Aumentar captacion de donaciones" + separator + 
                          "Contactar 3 nuevos donantes esta semana" + separator + 
                          "7 dias" + separator + "[ ]");
        }
        
        if (activas > 0 && completadas == 0) {
            writer.println("MEDIA" + separator + 
                          "Cero donaciones completadas" + separator + 
                          "Finalizar al menos una donacion activa" + separator + 
                          "3 dias" + separator + "[ ]");
        }
        
        if (total > 0 && activas > completadas * 2) {
            writer.println("MEDIA" + separator + 
                          "Muchas donaciones pendientes" + separator + 
                          "Revisar y acelerar procesos activos" + separator + 
                          "5 dias" + separator + "[ ]");
        }
        
        writer.println("BAJA" + separator + 
                      "Mantener reporte actualizado" + separator + 
                      "Exportar reporte semanalmente" + separator + 
                      "7 dias" + separator + "[ ]");
        writer.println();
        
        // 8. INSTRUCCIONES PARA EXCEL (en español simple)
        writer.println("# INSTRUCCIONES PARA EXCEL:");
        writer.println("# 1. Guardar como .csv con codificacion UTF-8");
        writer.println("# 2. En Excel: Datos > Texto en columnas > Delimitado > Punto y coma");
        writer.println("# 3. Seleccionar cada tabla y usar Ctrl+T para convertir en tabla");
        writer.println("# 4. Usar 'Formato como tabla' para aplicar estilos");
        writer.println("# 5. Los porcentajes estan listos para graficos");
        writer.println();
        
        // 9. METADATOS
        writer.println("=== METADATOS ===");
        writer.println("Campo" + separator + "Valor");
        writer.println("Generado automaticamente" + separator + "SI");
        writer.println("Usuario" + separator + escapeCSV(username, separator));
        writer.println("Fecha generacion" + separator + sdf.format(new Date()));
        writer.println("Formato" + separator + "CSV para Excel");
        writer.println("Separador" + separator + "Punto y coma (;)");
        writer.println("Codificacion" + separator + "UTF-8");
        writer.println("Version sistema" + separator + "2.0");
    }
    
    private String escapeCSV(String value, String separator) {
        if (value == null) return "";
        
        // Verificar si necesita comillas
        boolean needsQuotes = value.contains(separator) || 
                             value.contains("\"") || 
                             value.contains("\n") || 
                             value.contains("\r") ||
                             value.startsWith(" ") ||
                             value.endsWith(" ");
        
        if (needsQuotes) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        
        return value;
    }
}