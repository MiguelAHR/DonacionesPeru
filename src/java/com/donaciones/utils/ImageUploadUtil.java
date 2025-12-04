package com.donaciones.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;

public class ImageUploadUtil {
    
    /**
     * Sube una imagen a Catbox.moe
     * @param imageFile Archivo de imagen
     * @param fileName Nombre del archivo
     * @return URL de la imagen subida, o null si falla
     */
    public static String uploadToCatbox(File imageFile, String fileName) {
        try {
            // Verificar que el archivo existe
            if (!imageFile.exists() || !imageFile.isFile()) {
                throw new IOException("El archivo no existe o no es válido");
            }
            
            // Verificar tamaño máximo (5MB para Catbox)
            long fileSize = imageFile.length();
            if (fileSize > 5 * 1024 * 1024) { // 5MB
                throw new IOException("El archivo es demasiado grande (máximo 5MB)");
            }
            
            // Verificar tipo de archivo
            String mimeType = Files.probeContentType(imageFile.toPath());
            if (mimeType == null || !mimeType.startsWith("image/")) {
                throw new IOException("El archivo no es una imagen válida");
            }
            
            // Preparar conexión a Catbox.moe
            URL url = new URL("https://catbox.moe/user/api.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            
            // Configurar headers
            String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            
            // Crear cuerpo de la petición
            try (OutputStream output = conn.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true)) {
                
                // Campo reqtype
                writer.append("--").append(boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"reqtype\"\r\n\r\n");
                writer.append("fileupload\r\n");
                
                // Campo fileToUpload
                writer.append("--").append(boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"fileToUpload\"; filename=\"")
                      .append(URLEncoder.encode(fileName, "UTF-8")).append("\"\r\n");
                writer.append("Content-Type: ").append(mimeType).append("\r\n\r\n");
                writer.flush();
                
                // Escribir contenido del archivo
                try (FileInputStream input = new FileInputStream(imageFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                    output.flush();
                }
                
                writer.append("\r\n");
                writer.append("--").append(boundary).append("--\r\n");
                writer.flush();
            }
            
            // Leer respuesta
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
                    
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    
                    String imageUrl = response.toString().trim();
                    // Verificar que sea una URL válida
                    if (imageUrl.startsWith("http")) {
                        return imageUrl;
                    }
                }
            } else {
                System.err.println("Error de Catbox: " + responseCode);
            }
            
            conn.disconnect();
            
        } catch (Exception e) {
            System.err.println("Error al subir imagen a Catbox: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Alternativa: Guardar localmente si Catbox falla
     */
    public static String saveImageLocally(byte[] imageData, String fileName,
                                          String uploadPath) throws IOException {
        // Crear directorio si no existe
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        // Generar nombre único
        String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
        File imageFile = new File(uploadDir, uniqueFileName);
        
        // Guardar archivo
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            fos.write(imageData);
        }
        
        return "/uploads/" + uniqueFileName; // Ruta relativa para el navegador
    }
}