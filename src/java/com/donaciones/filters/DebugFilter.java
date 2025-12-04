package com.donaciones.filters;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class DebugFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                         FilterChain chain) throws IOException, ServletException {
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Crear respuesta envolvente para capturar datos
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(httpResponse) {
            private PrintWriter writer;
            private ServletOutputStream outputStream;
            
            @Override
            public PrintWriter getWriter() throws IOException {
                if (writer == null) {
                    writer = new PrintWriter(new OutputStreamWriter(baos, getCharacterEncoding()));
                }
                return writer;
            }
            
            @Override
            public ServletOutputStream getOutputStream() throws IOException {
                if (outputStream == null) {
                    outputStream = new ServletOutputStream() {
                        @Override
                        public void write(int b) throws IOException {
                            baos.write(b);
                        }
                        
                        @Override
                        public boolean isReady() {
                            return true;
                        }
                        
                        @Override
                        public void setWriteListener(WriteListener writeListener) {
                        }
                    };
                }
                return outputStream;
            }
            
            @Override
            public void flushBuffer() throws IOException {
                if (writer != null) writer.flush();
                if (outputStream != null) outputStream.flush();
            }
        };
        
        // Continuar la cadena
        chain.doFilter(request, responseWrapper);
        
        // Obtener lo que se escribió
        byte[] data = baos.toByteArray();
        
        System.out.println("\n=== DEBUG FILTER ===");
        System.out.println("Tamaño respuesta: " + data.length + " bytes");
        System.out.println("Content-Type: " + httpResponse.getContentType());
        
        // Mostrar primeros 200 bytes como texto
        String firstBytes = new String(data, 0, Math.min(data.length, 200));
        System.out.println("Primeros bytes: " + firstBytes);
        
        // Verificar si es Excel o HTML
        if (firstBytes.contains("<html") || firstBytes.contains("<!DOCTYPE")) {
            System.out.println("⚠️  ADVERTENCIA: Se está enviando HTML en lugar de Excel!");
            System.out.println("Contenido completo:");
            System.out.println(new String(data));
        } else if (data.length > 0) {
            System.out.println("✓ Parece ser contenido binario (posible Excel)");
            
            // Verificar firma de archivo Excel (.xlsx)
            if (data.length > 4) {
                // Los .xlsx comienzan con PK (archivo ZIP)
                if (data[0] == 0x50 && data[1] == 0x4B) { // "PK"
                    System.out.println("✓ Tiene firma ZIP (Excel .xlsx válido)");
                } else {
                    System.out.println("❌ NO tiene firma ZIP - No es Excel válido");
                }
            }
        }
        
        // Finalmente, escribir los datos a la respuesta real
        httpResponse.getOutputStream().write(data);
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}
    
    @Override
    public void destroy() {}
}