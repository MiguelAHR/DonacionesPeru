package com.donaciones.servlets;

import com.donaciones.dao.ProfileImageDAO;
import com.donaciones.models.ProfileImage;
import com.donaciones.models.User;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "ProfileImageServlet", urlPatterns = {"/uploadProfileImage"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,    // 1MB
    maxFileSize = 1024 * 1024 * 5,      // 5MB
    maxRequestSize = 1024 * 1024 * 10   // 10MB
)
public class ProfileImageServlet extends HttpServlet {
    
    // Carpeta donde se guardarán las imágenes
    private static final String UPLOAD_DIR = "uploads";
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            // Verificar sesión
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("ERROR:No estás autenticado");
                return;
            }
            
            User user = (User) session.getAttribute("user");
            
            // Obtener la imagen del formulario
            Part filePart = request.getPart("imageFile");
            if (filePart == null || filePart.getSize() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("ERROR:No se seleccionó ninguna imagen");
                return;
            }
            
            // Validar tamaño (5MB máximo)
            if (filePart.getSize() > 5 * 1024 * 1024) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("ERROR:La imagen es muy grande (máximo 5MB)");
                return;
            }
            
            // Obtener nombre del archivo
            String fileName = getFileName(filePart);
            
            // Validar extensión
            if (!isValidImage(fileName)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("ERROR:Solo se permiten imágenes JPG, PNG o GIF");
                return;
            }
            
            // Crear directorio de uploads si no existe
            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // Generar nombre único para el archivo
            String uniqueFileName = user.getId() + "_" + System.currentTimeMillis() + getFileExtension(fileName);
            
            // Guardar archivo
            String filePath = uploadPath + File.separator + uniqueFileName;
            filePart.write(filePath);
            
            // Ruta relativa para la base de datos
            String imageUrl = request.getContextPath() + "/" + UPLOAD_DIR + "/" + uniqueFileName;
            
            // Guardar en base de datos
            ProfileImageDAO dao = new ProfileImageDAO();
            ProfileImage profileImage = new ProfileImage(user.getId(), imageUrl);
            boolean success = dao.uploadImage(profileImage);
            
            if (success) {
                out.print("SUCCESS:" + imageUrl);
                // Actualizar en sesión
                session.setAttribute("profileImageUrl", imageUrl);
            } else {
                // Si falla, borrar el archivo subido
                new File(filePath).delete();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("ERROR:No se pudo guardar en la base de datos");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("ERROR:" + e.getMessage());
        } finally {
            out.flush();
            out.close();
        }
    }
    
    // Método para obtener imagen actual
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            // Verificar sesión
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("ERROR:No estás autenticado");
                return;
            }
            
            User user = (User) session.getAttribute("user");
            ProfileImageDAO dao = new ProfileImageDAO();
            ProfileImage image = dao.getActiveImageByUser(user.getId());
            
            if (image != null && image.getImageUrl() != null) {
                out.print("SUCCESS:" + image.getImageUrl());
            } else {
                // URL por defecto
                String defaultImage = request.getContextPath() + "/assets/images/default-avatar.png";
                out.print("SUCCESS:" + defaultImage);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            out.print("ERROR:" + e.getMessage());
        } finally {
            out.flush();
            out.close();
        }
    }
    
    // Métodos auxiliares
    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        if (contentDisp == null) return "";
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length() - 1);
            }
        }
        return "";
    }
    
    private boolean isValidImage(String fileName) {
        if (fileName == null || fileName.isEmpty()) return false;
        String lower = fileName.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || 
               lower.endsWith(".png") || lower.endsWith(".gif");
    }
    
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) return "";
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }
}