package com.donaciones.servlets;

import com.donaciones.dao.CatalogoDAO;
import com.donaciones.models.Catalogo;
import com.donaciones.utils.FileUploadUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,    // 1MB
    maxFileSize = 5 * 1024 * 1024,      // 5MB para catálogo
    maxRequestSize = 10 * 1024 * 1024   // 10MB
)
public class ImageUploadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        String catalogIdParam = request.getParameter("catalogId");
        
        try {
            if ("upload".equals(action) && catalogIdParam != null) {
                int catalogId = Integer.parseInt(catalogIdParam);
                uploadImage(request, response, catalogId);
            } else if ("delete".equals(action) && catalogIdParam != null) {
                int catalogId = Integer.parseInt(catalogIdParam);
                deleteImage(request, response, catalogId);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Acción no válida");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error procesando la imagen: " + e.getMessage());
        }
    }
    
    private void uploadImage(HttpServletRequest request, HttpServletResponse response, int catalogId)
            throws ServletException, IOException {
        
        // Obtener archivo subido
        Part filePart = request.getPart("imageFile");
        
        if (filePart == null || filePart.getSize() == 0) {
            response.sendRedirect(request.getContextPath() + 
                "/adminCatalog?action=edit&id=" + catalogId + "&error=no_file");
            return;
        }
        
        // Obtener contexto para ruta de almacenamiento
        String contextPath = getServletContext().getRealPath("");
        CatalogoDAO catalogoDAO = new CatalogoDAO();
        
        try {
            // Obtener item del catálogo
            Catalogo catalogo = catalogoDAO.getCatalogoItem(catalogId);
            if (catalogo == null) {
                response.sendRedirect(request.getContextPath() + 
                    "/adminCatalog?action=edit&id=" + catalogId + "&error=item_not_found");
                return;
            }
            
            // Eliminar imagen anterior si existe y es personalizada
            if (catalogo.hasCustomImage()) {
                FileUploadUtil.deleteFile(catalogo.getImagen(), contextPath);
            }
            
            // Guardar nueva imagen - CORRECCIÓN: usar saveCatalogImage
            String imagePath = FileUploadUtil.saveCatalogImage(filePart, contextPath, catalogo.getTitulo());
            
            if (imagePath != null) {
                // Actualizar en base de datos
                catalogo.setImagen(imagePath);
                boolean updated = catalogoDAO.updateCatalogoItem(catalogo);
                
                if (updated) {
                    response.sendRedirect(request.getContextPath() + 
                        "/adminCatalog?action=edit&id=" + catalogId + "&success=image_uploaded");
                } else {
                    // Si falla BD, eliminar archivo subido
                    FileUploadUtil.deleteFile(imagePath, contextPath);
                    response.sendRedirect(request.getContextPath() + 
                        "/adminCatalog?action=edit&id=" + catalogId + "&error=db_update_failed");
                }
            } else {
                response.sendRedirect(request.getContextPath() + 
                    "/adminCatalog?action=edit&id=" + catalogId + "&error=upload_failed");
            }
            
        } catch (IOException e) {
            String errorMsg = e.getMessage() != null ? e.getMessage().replace(" ", "_") : "upload_error";
            response.sendRedirect(request.getContextPath() + 
                "/adminCatalog?action=edit&id=" + catalogId + "&error=" + errorMsg);
        }
    }
    
    private void deleteImage(HttpServletRequest request, HttpServletResponse response, int catalogId)
            throws ServletException, IOException {
        
        String contextPath = getServletContext().getRealPath("");
        CatalogoDAO catalogoDAO = new CatalogoDAO();
        
        try {
            Catalogo catalogo = catalogoDAO.getCatalogoItem(catalogId);
            
            if (catalogo != null && catalogo.hasCustomImage()) {
                // Eliminar archivo físico
                FileUploadUtil.deleteFile(catalogo.getImagen(), contextPath);
                
                // Restaurar imagen por defecto en BD - CORRECCIÓN: usar getDefaultCatalogImage
                String defaultImage = FileUploadUtil.getDefaultCatalogImage(catalogo.getTipo());
                catalogo.setImagen(defaultImage);
                boolean updated = catalogoDAO.updateCatalogoItem(catalogo);
                
                if (updated) {
                    response.sendRedirect(request.getContextPath() + 
                        "/adminCatalog?action=edit&id=" + catalogId + "&success=image_deleted");
                } else {
                    response.sendRedirect(request.getContextPath() + 
                        "/adminCatalog?action=edit&id=" + catalogId + "&error=db_update_failed");
                }
            } else {
                response.sendRedirect(request.getContextPath() + 
                    "/adminCatalog?action=edit&id=" + catalogId + "&error=no_custom_image");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + 
                "/adminCatalog?action=edit&id=" + catalogId + "&error=delete_failed");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirigir si alguien intenta acceder por GET
        response.sendRedirect(request.getContextPath() + "/adminCatalog?action=list");
    }
}