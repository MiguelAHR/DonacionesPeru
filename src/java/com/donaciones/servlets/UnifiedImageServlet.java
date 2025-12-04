package com.donaciones.servlets;

import com.donaciones.dao.UserDAO;
import com.donaciones.models.User;
import com.donaciones.utils.FileUploadUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

// NOTA: La anotación @MultipartConfig se eliminó porque la configuración
// está en el web.xml con <multipart-config>
public class UnifiedImageServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("DEBUG UnifiedImageServlet - Iniciando doPost");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            System.out.println("DEBUG UnifiedImageServlet - No hay sesión activa");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String username = (String) session.getAttribute("username");
        String userType = (String) session.getAttribute("userType");
        String action = request.getParameter("action");
        String imageType = request.getParameter("imageType"); // "profile" o "catalog"
        
        System.out.println("DEBUG UnifiedImageServlet - Usuario: " + username + 
                          ", Tipo: " + userType + 
                          ", Acción: " + action + 
                          ", ImageType: " + imageType);
        
        try {
            if ("upload".equals(action) && "profile".equals(imageType)) {
                uploadProfileImage(request, response, username, userType);
            } else if ("delete".equals(action) && "profile".equals(imageType)) {
                deleteProfileImage(request, response, username, userType);
            } else {
                System.out.println("DEBUG UnifiedImageServlet - Acción o tipo inválido");
                redirectToUserPage(request, response, userType, "error=invalid_action");
            }
        } catch (Exception e) {
            System.out.println("ERROR UnifiedImageServlet - Excepción: " + e.getMessage());
            e.printStackTrace();
            redirectToUserPage(request, response, userType, "error=upload_failed&msg=" + 
                            e.getMessage().replace(" ", "_"));
        }
    }
    
    private void uploadProfileImage(HttpServletRequest request, HttpServletResponse response, 
                                   String username, String userType) throws ServletException, IOException {
        
        System.out.println("DEBUG UnifiedImageServlet - Subiendo imagen de perfil para: " + username);
        
        Part filePart = request.getPart("profileImage");
        
        if (filePart == null || filePart.getSize() == 0) {
            System.out.println("DEBUG UnifiedImageServlet - No se envió archivo");
            redirectToUserPage(request, response, userType, "error=no_file");
            return;
        }
        
        // Validar tamaño (2MB máximo) - también configurado en web.xml
        if (filePart.getSize() > 2 * 1024 * 1024) {
            System.out.println("DEBUG UnifiedImageServlet - Archivo demasiado grande: " + filePart.getSize());
            redirectToUserPage(request, response, userType, "error=file_too_large");
            return;
        }
        
        UserDAO userDAO = new UserDAO();
        
        try {
            User user = userDAO.getUserByUsername(username);
            if (user == null) {
                System.out.println("DEBUG UnifiedImageServlet - Usuario no encontrado: " + username);
                redirectToUserPage(request, response, userType, "error=user_not_found");
                return;
            }
            
            // Eliminar imagen anterior si existe y es personalizada
            String oldImage = user.getProfileImage();
            if (oldImage != null && !oldImage.isEmpty() && FileUploadUtil.isCustomImage(oldImage)) {
                String contextPath = getServletContext().getRealPath("");
                boolean deleted = FileUploadUtil.deleteFile(oldImage, contextPath);
                System.out.println("DEBUG UnifiedImageServlet - Imagen anterior eliminada: " + deleted);
            }
            
            // Guardar nueva imagen usando FileUploadUtil
            String contextPath = getServletContext().getRealPath("");
            String relativePath = FileUploadUtil.saveProfileImage(filePart, contextPath, username);
            
            if (relativePath == null) {
                System.out.println("DEBUG UnifiedImageServlet - Error al guardar imagen");
                redirectToUserPage(request, response, userType, "error=upload_failed");
                return;
            }
            
            System.out.println("DEBUG UnifiedImageServlet - Imagen guardada en: " + relativePath);
            
            // Actualizar en base de datos
            boolean updated = userDAO.updateUserProfileImage(user.getId(), relativePath);
            
            if (updated) {
                System.out.println("DEBUG UnifiedImageServlet - BD actualizada con: " + relativePath);
                
                // Actualizar en sesión
                updateSessionImage(request, relativePath);
                
                redirectToUserPage(request, response, userType, "success=image_uploaded");
            } else {
                // Si falla BD, eliminar archivo subido
                FileUploadUtil.deleteFile(relativePath, contextPath);
                System.out.println("DEBUG UnifiedImageServlet - Error al actualizar BD");
                redirectToUserPage(request, response, userType, "error=db_update_failed");
            }
            
        } catch (Exception e) {
            System.out.println("ERROR UnifiedImageServlet - Error en upload: " + e.getMessage());
            throw e;
        }
    }
    
    private void deleteProfileImage(HttpServletRequest request, HttpServletResponse response, 
                                   String username, String userType) throws ServletException, IOException {
        
        System.out.println("DEBUG UnifiedImageServlet - Eliminando imagen para: " + username);
        
        UserDAO userDAO = new UserDAO();
        
        try {
            User user = userDAO.getUserByUsername(username);
            
            if (user != null) {
                String currentImage = user.getProfileImage();
                System.out.println("DEBUG UnifiedImageServlet - Imagen actual: " + currentImage);
                
                // Verificar si tiene imagen personalizada
                boolean isCustomImage = FileUploadUtil.isCustomImage(currentImage);
                
                System.out.println("DEBUG UnifiedImageServlet - Es imagen personalizada: " + isCustomImage);
                
                if (isCustomImage) {
                    // Eliminar archivo físico
                    String contextPath = getServletContext().getRealPath("");
                    boolean deleted = FileUploadUtil.deleteFile(currentImage, contextPath);
                    System.out.println("DEBUG UnifiedImageServlet - Archivo eliminado: " + deleted);
                }
                
                // Restaurar imagen por defecto según tipo de usuario
                String defaultImage = FileUploadUtil.getDefaultProfileImage(userType);
                
                // Actualizar en base de datos
                boolean updated = userDAO.updateUserProfileImage(user.getId(), defaultImage);
                
                if (updated) {
                    // Actualizar en sesión
                    updateSessionImage(request, defaultImage);
                    
                    System.out.println("DEBUG UnifiedImageServlet - Imagen restaurada a: " + defaultImage);
                    redirectToUserPage(request, response, userType, "success=image_deleted");
                } else {
                    System.out.println("DEBUG UnifiedImageServlet - Error al actualizar BD");
                    redirectToUserPage(request, response, userType, "error=db_update_failed");
                }
            } else {
                System.out.println("DEBUG UnifiedImageServlet - Usuario no encontrado");
                redirectToUserPage(request, response, userType, "error=user_not_found");
            }
            
        } catch (Exception e) {
            System.out.println("ERROR UnifiedImageServlet - Error al eliminar: " + e.getMessage());
            throw e;
        }
    }
    
    private void updateSessionImage(HttpServletRequest request, String imagePath) {
        HttpSession session = request.getSession();
        String contextPath = request.getContextPath();
        
        // Construir ruta completa para el navegador
        String fullImagePath = FileUploadUtil.buildFullImageUrl(contextPath, imagePath);
        
        session.setAttribute("profileImage", fullImagePath);
        
        System.out.println("DEBUG UnifiedImageServlet - Sesión actualizada con: " + fullImagePath);
    }
    
    private void redirectToUserPage(HttpServletRequest request, HttpServletResponse response, 
                                    String userType, String params) throws IOException {
        
        String contextPath = request.getContextPath();
        String redirectPath = getDashboardPath(userType);
        
        System.out.println("DEBUG UnifiedImageServlet - Redirigiendo a: " + redirectPath + "?" + params);
        
        response.sendRedirect(contextPath + redirectPath + "?" + params);
    }
    
    private String getDashboardPath(String userType) {
        if (userType == null) {
            return "/dashboard";
        }
        
        switch (userType.toLowerCase()) {
            case "admin":
            case "empleado":
                return "/profile";
            case "usuario":
            case "donador":
            case "receptor":
            default:
                return "/dashboard";
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirigir al dashboard si alguien intenta acceder por GET
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}