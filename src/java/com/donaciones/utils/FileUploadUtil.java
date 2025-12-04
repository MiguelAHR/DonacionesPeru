package com.donaciones.utils;

import com.donaciones.models.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import javax.servlet.http.Part;

public class FileUploadUtil {
    
    // Directorios UNIFICADOS
    private static final String UPLOAD_DIR = "uploads";
    private static final String PROFILE_IMAGES_DIR = "profile-images";
    private static final String CATALOG_IMAGES_DIR = "catalog-images";
    
    // Extensiones permitidas
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "webp"};
    
    // Tamaños máximos (en bytes)
    private static final long MAX_PROFILE_IMAGE_SIZE = 2 * 1024 * 1024; // 2MB
    private static final long MAX_CATALOG_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    
    /**
     * Guarda una imagen de perfil subida - MÉTODO UNIFICADO
     */
    public static String saveProfileImage(Part filePart, String contextPath, String username) throws IOException {
        return saveImage(filePart, contextPath, PROFILE_IMAGES_DIR, MAX_PROFILE_IMAGE_SIZE, username);
    }
    
    /**
     * Guarda una imagen del catálogo subida
     */
    public static String saveCatalogImage(Part filePart, String contextPath, String itemTitle) throws IOException {
        return saveImage(filePart, contextPath, CATALOG_IMAGES_DIR, MAX_CATALOG_IMAGE_SIZE, itemTitle);
    }
    
    /**
     * Método genérico para guardar imágenes - UNIFICADO
     */
    private static String saveImage(Part filePart, String contextPath, String subDir, long maxSize, String identifier) 
            throws IOException {
        
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }
        
        // Validar tamaño
        validateFileSize(filePart, maxSize);
        
        // Validar extensión
        String fileName = getFileName(filePart);
        String fileExtension = getFileExtension(fileName).toLowerCase();
        validateFileExtension(fileExtension);
        
        // Crear directorios
        String uploadPath = createDirectories(contextPath, subDir);
        
        // Generar nombre único y guardar
        return saveFile(filePart, uploadPath, fileExtension, identifier);
    }
    
    /**
     * Obtiene el nombre del archivo desde el Part
     */
    private static String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) {
            return null;
        }
        String[] items = contentDisposition.split(";");
        for (String item : items) {
            if (item.trim().startsWith("filename")) {
                return item.substring(item.indexOf("=") + 2, item.length() - 1);
            }
        }
        return null;
    }
    
    /**
     * Valida el tamaño del archivo
     */
    private static void validateFileSize(Part filePart, long maxSize) throws IOException {
        if (filePart.getSize() > maxSize) {
            long sizeInMB = maxSize / (1024 * 1024);
            throw new IOException("El archivo excede el tamaño máximo de " + sizeInMB + "MB");
        }
    }
    
    /**
     * Valida la extensión del archivo
     */
    private static void validateFileExtension(String extension) throws IOException {
        boolean allowed = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equalsIgnoreCase(extension)) {
                allowed = true;
                break;
            }
        }
        
        if (!allowed) {
            throw new IOException("Tipo de archivo no permitido. Use: " + 
                String.join(", ", ALLOWED_EXTENSIONS));
        }
    }
    
    /**
     * Crea los directorios necesarios - UNIFICADO
     */
    public static String createDirectories(String contextPath, String subDir) {
        // Directorio base de uploads
        String uploadPath = contextPath + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            System.out.println("DEBUG FileUploadUtil - Directorio uploads creado: " + created + " en: " + uploadPath);
        }
        
        // Subdirectorio específico
        String specificPath = uploadPath + File.separator + subDir;
        File specificDir = new File(specificPath);
        if (!specificDir.exists()) {
            boolean created = specificDir.mkdirs();
            System.out.println("DEBUG FileUploadUtil - Directorio " + subDir + " creado: " + created + " en: " + specificPath);
        }
        
        // Subdirectorios por año/mes
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy" + File.separator + "MM");
        String datePath = dateFormat.format(new Date());
        File dateDir = new File(specificDir, datePath);
        if (!dateDir.exists()) {
            boolean created = dateDir.mkdirs();
            System.out.println("DEBUG FileUploadUtil - Directorio fecha creado: " + created + " en: " + dateDir.getAbsolutePath());
        }
        
        return specificPath;
    }
    
    /**
     * Guarda el archivo en el sistema - UNIFICADO
     */
    private static String saveFile(Part filePart, String basePath, String extension, String identifier) 
            throws IOException {
        
        // Crear subdirectorios por año/mes
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy" + File.separator + "MM");
        String datePath = dateFormat.format(new Date());
        
        // Generar nombre único CON FORMATO UNIFICADO
        String uniqueFileName = generateUniqueFileName(identifier, extension);
        String filePath = datePath + File.separator + uniqueFileName;
        File storeFile = new File(basePath, filePath);
        
        // Guardar archivo
        try (InputStream input = filePart.getInputStream()) {
            Files.copy(input, storeFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        
        // Devolver ruta relativa para BD (usando / como separador) - FORMATO UNIFICADO
        String relativePath = "/" + UPLOAD_DIR + "/" + 
                            new File(basePath).getName() + "/" + 
                            filePath.replace(File.separator, "/");
        
        System.out.println("DEBUG FileUploadUtil - Archivo guardado en: " + storeFile.getAbsolutePath());
        System.out.println("DEBUG FileUploadUtil - Ruta relativa para BD: " + relativePath);
        
        return relativePath;
    }
    
    /**
     * Elimina un archivo del servidor - MEJORADO
     */
    public static boolean deleteFile(String filePath, String contextPath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return true;
        }
        
        // No eliminar imágenes por defecto
        if (!isCustomImage(filePath)) {
            System.out.println("DEBUG FileUploadUtil - No se elimina imagen por defecto: " + filePath);
            return true;
        }
        
        try {
            // Procesar diferentes formatos de ruta
            String relativePath = filePath;
            
            // Si es una ruta de sesión (con contexto), extraer la parte relativa
            if (filePath.contains("/uploads/")) {
                int uploadIndex = filePath.indexOf("/uploads/");
                relativePath = filePath.substring(uploadIndex);
            }
            
            // Remover "/" inicial si existe
            if (relativePath.startsWith("/")) {
                relativePath = relativePath.substring(1);
            }
            
            File file = new File(contextPath, relativePath);
            
            if (file.exists()) {
                boolean deleted = file.delete();
                System.out.println("DEBUG FileUploadUtil - Archivo eliminado " + file.getAbsolutePath() + ": " + deleted);
                return deleted;
            } else {
                // Intentar con ruta directa (para compatibilidad con sistema viejo)
                File directFile = new File(contextPath, filePath);
                if (directFile.exists()) {
                    boolean deleted = directFile.delete();
                    System.out.println("DEBUG FileUploadUtil - Archivo eliminado (ruta directa) " + directFile.getAbsolutePath() + ": " + deleted);
                    return deleted;
                }
                
                System.out.println("DEBUG FileUploadUtil - Archivo no encontrado: " + relativePath);
                return true; // Si no existe, considerarlo éxito
            }
        } catch (SecurityException e) {
            System.err.println("ERROR FileUploadUtil - Error de seguridad eliminando archivo: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("ERROR FileUploadUtil - Error eliminando archivo " + filePath + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica si una imagen es personalizada - MEJORADO
     */
    public static boolean isCustomImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return false;
        }
        
        // Verificar si es una imagen por defecto
        return !imagePath.contains("default-profile") &&
               !imagePath.contains("donor-profile") &&
               !imagePath.contains("receiver-profile") &&
               !imagePath.contains("admin-profile") &&
               !imagePath.contains("employee-profile") &&
               !imagePath.contains("user-profile") &&
               !imagePath.contains("default-donation") &&
               !imagePath.contains("ropa-default") &&
               !imagePath.contains("cuadernos-default") &&
               !imagePath.contains("utiles-default") &&
               !imagePath.contains("reciclable-default") &&
               !imagePath.contains("ropa-nueva-default");
    }
    
    /**
     * Obtiene la imagen por defecto para el tipo de usuario - MEJORADO
     */
    public static String getDefaultProfileImage(String userType) {
        if (userType == null) {
            return "/images/default-profile.png";
        }
        
        switch (userType.toLowerCase()) {
            case "admin":
                return "/images/admin-profile.png";
            case "empleado":
                return "/images/employee-profile.png";
            case "donador":
                return "/images/donor-profile.png";
            case "receptor":
                return "/images/receiver-profile.png";
            case "usuario":
                return "/images/user-profile.png";
            default:
                return "/images/default-profile.png";
        }
    }
    
    /**
     * Obtiene la imagen por defecto para catálogo
     */
    public static String getDefaultCatalogImage(String tipo) {
        if (tipo == null) {
            return "/images/default-donation.png";
        }
        
        switch (tipo.toLowerCase()) {
            case "ropa":
                return "/images/ropa-default.png";
            case "cuadernos":
                return "/images/cuadernos-default.png";
            case "utiles_escolares":
            case "utiles escolares":
                return "/images/utiles-default.png";
            case "material_reciclable":
            case "material reciclable":
                return "/images/reciclable-default.png";
            case "ropa_casi_nueva":
            case "ropa casi nueva":
                return "/images/ropa-nueva-default.png";
            default:
                return "/images/default-donation.png";
        }
    }
    
    /**
     * Obtiene la extensión de un archivo
     */
    private static String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }
    
    /**
     * Genera un nombre único para el archivo - FORMATO UNIFICADO
     */
    private static String generateUniqueFileName(String identifier, String extension) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        // Crear identificador seguro
        String safeIdentifier = "file";
        if (identifier != null && !identifier.trim().isEmpty()) {
            safeIdentifier = identifier.replaceAll("[^a-zA-Z0-9._-]", "_");
            safeIdentifier = safeIdentifier.substring(0, Math.min(20, safeIdentifier.length()));
        }
        
        return safeIdentifier + "_" + timestamp + "_" + uuid + "." + extension.toLowerCase();
    }
    
    /**
     * Construye la URL completa para una imagen - MEJORADO
     */
    public static String buildFullImageUrl(String contextPath, String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return contextPath + "/images/default-profile.png";
        }
        
        // Si ya es una URL completa o empieza con http, devolver tal cual
        if (imagePath.startsWith("http") || imagePath.startsWith("//")) {
            return imagePath;
        }
        
        // Si empieza con /, agregar contextPath
        if (imagePath.startsWith("/")) {
            return contextPath + imagePath;
        }
        
        // Si no, agregar contextPath y /
        return contextPath + "/" + imagePath;
    }
    
    /**
     * Convierte rutas del sistema viejo al nuevo formato
     */
    public static String convertOldPathToNew(String oldPath) {
        if (oldPath == null || oldPath.isEmpty()) {
            return getDefaultProfileImage(null);
        }
        
        // Si ya está en el nuevo formato, devolver tal cual
        if (oldPath.contains("/uploads/profile-images/")) {
            return oldPath;
        }
        
        // Si es del sistema viejo (uploads/profiles/), convertir
        if (oldPath.contains("uploads/profiles/")) {
            String fileName = oldPath.substring(oldPath.lastIndexOf("/") + 1);
            // Extraer username y timestamp del nombre viejo
            String username = fileName.contains("_") ? fileName.substring(0, fileName.indexOf("_")) : "user";
            
            // Crear nueva ruta
            String newPath = "/uploads/profile-images/2025/12/" + 
                           username + "_" + System.currentTimeMillis() + "_" + 
                           UUID.randomUUID().toString().substring(0, 8) + 
                           getFileExtension(fileName);
            
            System.out.println("DEBUG FileUploadUtil - Convirtiendo ruta vieja: " + oldPath + " -> " + newPath);
            return newPath;
        }
        
        // Si es una imagen por defecto, devolver tal cual
        return oldPath;
    }
    
    /**
     * Obtiene la imagen de perfil con manejo de rutas mixtas - CORREGIDO
     */
    public static String getProfileImageForSession(User user, String userType, String contextPath) {
        if (user == null) {
            return buildFullImageUrl(contextPath, getDefaultProfileImage(userType));
        }
        
        String profileImage = user.getProfileImage();
        
        // Si no hay imagen, usar por defecto
        if (profileImage == null || profileImage.isEmpty()) {
            return buildFullImageUrl(contextPath, getDefaultProfileImage(userType));
        }
        
        // Convertir rutas viejas al nuevo formato para consistencia
        if (profileImage.contains("uploads/profiles/")) {
            profileImage = convertOldPathToNew(profileImage);
        }
        
        // Asegurar que la ruta sea completa para el navegador
        return buildFullImageUrl(contextPath, profileImage);
    }
}