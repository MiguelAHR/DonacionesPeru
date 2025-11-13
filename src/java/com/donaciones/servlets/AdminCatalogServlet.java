package com.donaciones.servlets;

import com.donaciones.utils.DataManager;
import com.donaciones.models.Catalogo;
import com.donaciones.models.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "AdminCatalogServlet", urlPatterns = {"/adminCatalog"})
public class AdminCatalogServlet extends HttpServlet {

    private DataManager dataManager;

    @Override
    public void init() throws ServletException {
        try {
            dataManager = DataManager.getInstance();
            System.out.println("DEBUG - DataManager inicializado correctamente en AdminCatalogServlet");
        } catch (Exception e) {
            System.err.println("ERROR - No se pudo inicializar DataManager: " + e.getMessage());
            throw new ServletException("Error inicializando DataManager", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getUserType())) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "view";
        }

        try {
            switch (action) {
                case "view":
                    viewCatalog(request, response);
                    break;
                case "viewItem":
                    viewCatalogItem(request, response);
                    break;
                case "getItemData":
                    getItemData(request, response);
                    break;
                default:
                    viewCatalog(request, response);
                    break;
            }
        } catch (Exception e) {
            System.out.println("ERROR AdminCatalogServlet - Error en GET: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/adminCatalog?error=server_error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getUserType())) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/adminCatalog");
            return;
        }

        try {
            switch (action) {
                case "updateItem":
                    updateCatalogItem(request, response);
                    break;
                case "updateStatus":
                    updateItemStatus(request, response);
                    break;
                case "deleteItem":
                    deleteCatalogItem(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/adminCatalog");
            }
        } catch (Exception e) {
            System.out.println("ERROR AdminCatalogServlet - Error en POST: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/adminCatalog?error=server_error");
        }
    }

    // ========== MÉTODOS PRINCIPALES ==========

    private void viewCatalog(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("DEBUG AdminCatalogServlet - Iniciando viewCatalog");
        
        try {
            // Obtener todos los ítems del catálogo
            List<Catalogo> catalogItems = dataManager.getAllCatalogItems();
            System.out.println("DEBUG - Total de ítems obtenidos: " + (catalogItems != null ? catalogItems.size() : "null"));
            
            if (catalogItems == null) {
                catalogItems = new java.util.ArrayList<>();
            }

            // Aplicar filtros si existen
            String statusFilter = request.getParameter("status");
            String typeFilter = request.getParameter("type");
            
            System.out.println("DEBUG AdminCatalogServlet - Filtros: status=" + statusFilter + ", type=" + typeFilter);
            
            if (statusFilter != null && !statusFilter.isEmpty()) {
                final String filter = statusFilter;
                catalogItems = catalogItems.stream()
                        .filter(item -> filter.equals(item.getEstado()))
                        .collect(java.util.stream.Collectors.toList());
                System.out.println("DEBUG AdminCatalogServlet - Después de filtrar por estado: " + catalogItems.size());
            }
            
            if (typeFilter != null && !typeFilter.isEmpty()) {
                final String filter = typeFilter;
                catalogItems = catalogItems.stream()
                        .filter(item -> filter.equals(item.getTipo()))
                        .collect(java.util.stream.Collectors.toList());
                System.out.println("DEBUG AdminCatalogServlet - Después de filtrar por tipo: " + catalogItems.size());
            }
            
            request.setAttribute("catalogItems", catalogItems);
            request.setAttribute("currentStatus", statusFilter);
            request.setAttribute("currentType", typeFilter);
            
            System.out.println("DEBUG AdminCatalogServlet - Redirigiendo a JSP con " + catalogItems.size() + " ítems");
            
            request.getRequestDispatcher("/WEB-INF/views/admin/catalog_management.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("ERROR CRÍTICO en viewCatalog: " + e.getMessage());
            e.printStackTrace();
            // En caso de error, enviar lista vacía
            request.setAttribute("catalogItems", new java.util.ArrayList<Catalogo>());
            request.setAttribute("error", "Error al cargar el catálogo: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/catalog_management.jsp").forward(request, response);
        }
    }

    private void viewCatalogItem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect(request.getContextPath() + "/adminCatalog?error=invalid_id");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Catalogo catalogItem = dataManager.getCatalogoItem(id);
            
            if (catalogItem == null) {
                response.sendRedirect(request.getContextPath() + "/adminCatalog?error=not_found");
                return;
            }

            request.setAttribute("catalogItem", catalogItem);
            request.getRequestDispatcher("/WEB-INF/views/admin/catalog_item_details.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/adminCatalog?error=invalid_id");
        }
    }

    private void getItemData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID requerido");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Catalogo catalogItem = dataManager.getCatalogoItem(id);
            
            if (catalogItem == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ítem no encontrado");
                return;
            }

            // Enviar datos como JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            String json = String.format(
                "{\"id\":%d,\"titulo\":\"%s\",\"descripcion\":\"%s\",\"tipo\":\"%s\",\"cantidad\":%d,\"condicion\":\"%s\",\"ubicacion\":\"%s\",\"estado\":\"%s\",\"prioridad\":%d}",
                catalogItem.getId(),
                escapeJson(catalogItem.getTitulo()),
                escapeJson(catalogItem.getDescripcion()),
                escapeJson(catalogItem.getTipo()),
                catalogItem.getCantidad(),
                escapeJson(catalogItem.getCondicion()),
                escapeJson(catalogItem.getUbicacion()),
                escapeJson(catalogItem.getEstado()),
                catalogItem.getPrioridad()
            );
            
            response.getWriter().write(json);
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error del servidor");
        }
    }

    // ========== MÉTODOS POST ==========

    private void updateCatalogItem(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        try {
            int itemId = Integer.parseInt(request.getParameter("itemId"));
            Catalogo existingItem = dataManager.getCatalogoItem(itemId);
            
            if (existingItem == null) {
                response.sendRedirect(request.getContextPath() + "/adminCatalog?error=not_found");
                return;
            }

            // Actualizar campos del ítem
            existingItem.setTitulo(request.getParameter("titulo"));
            existingItem.setDescripcion(request.getParameter("descripcion"));
            existingItem.setTipo(request.getParameter("tipo"));
            existingItem.setCantidad(Integer.parseInt(request.getParameter("cantidad")));
            existingItem.setCondicion(request.getParameter("condicion"));
            existingItem.setUbicacion(request.getParameter("ubicacion"));
            existingItem.setPrioridad(Integer.parseInt(request.getParameter("prioridad")));
            
            String estado = request.getParameter("estado");
            if (estado != null && !estado.isEmpty()) {
                existingItem.setEstado(estado);
            }

            boolean success = dataManager.updateCatalogoItem(existingItem);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/adminCatalog?success=item_updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/adminCatalog?error=update_failed");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/adminCatalog?error=invalid_data");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/adminCatalog?error=server_error");
        }
    }

    private void updateItemStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            int itemId = Integer.parseInt(request.getParameter("itemId"));
            String nuevoEstado = request.getParameter("estado");
            
            if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/adminCatalog?error=invalid_status");
                return;
            }

            boolean success = dataManager.updateCatalogoItemStatus(itemId, nuevoEstado);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/adminCatalog?success=status_updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/adminCatalog?error=status_update_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/adminCatalog?error=invalid_id");
        }
    }

    // MÉTODO CORREGIDO - ELIMINACIÓN PERMANENTE
    private void deleteCatalogItem(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            int itemId = Integer.parseInt(request.getParameter("itemId"));
            
            // ELIMINACIÓN PERMANENTE del ítem
            boolean success = dataManager.deleteCatalogoItem(itemId);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/adminCatalog?success=item_deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/adminCatalog?error=delete_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/adminCatalog?error=invalid_id");
        }
    }

    // Método auxiliar para escapar JSON
    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}