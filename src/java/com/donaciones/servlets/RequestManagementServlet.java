package com.donaciones.servlets;

import com.donaciones.models.Request;
import com.donaciones.utils.DataManager;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RequestManagementServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String userType = (String) session.getAttribute("userType");
        if (!"admin".equals(userType)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        String action = request.getParameter("action");
        DataManager dm = DataManager.getInstance();

        switch (action) {
            case "updateStatus":
                updateRequestStatus(request, response, dm);
                break;
            case "assignEmployee":
                assignEmployeeToRequest(request, response, dm);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/requestManagement");
        }
    }

// Actualizar doGet para modo solo lectura
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String userType = (String) session.getAttribute("userType");
        if (!"admin".equals(userType) && !"empleado".equals(userType)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        request.setAttribute("readOnlyMode", !"admin".equals(userType));

        DataManager dm = DataManager.getInstance();
        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "list":
                List<Request> requests = dm.getAllRequests();
                request.setAttribute("requests", requests);
                request.setAttribute("pendingRequests", dm.getRequestsByStatus("pending").size());
                request.setAttribute("inProgressRequests", dm.getRequestsByStatus("in_progress").size());

                if ("admin".equals(userType)) {
                    request.getRequestDispatcher("/WEB-INF/views/admin/request_management.jsp").forward(request, response);
                } else {
                    request.getRequestDispatcher("/WEB-INF/views/empleado/request_management.jsp").forward(request, response);
                }
                break;

            case "edit":
                // Solo admin puede editar
                if (!"admin".equals(userType)) {
                    response.sendRedirect(request.getContextPath() + "/requestManagement?error=no_permission");
                    return;
                }
                // ... resto del código de edición
                break;

            default:
                response.sendRedirect(request.getContextPath() + "/requestManagement?action=list");
        }
    }

    private void updateRequestStatus(HttpServletRequest request, HttpServletResponse response, DataManager dm)
            throws IOException {

        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String newStatus = request.getParameter("status");
            String notes = request.getParameter("notes");

            Request req = dm.getRequest(requestId);
            if (req != null) {
                req.setStatus(newStatus);
                if (notes != null && !notes.trim().isEmpty()) {
                    req.setNotes(notes);
                }
                dm.updateRequest(req);

                response.sendRedirect(request.getContextPath() + "/requestManagement?success=status_updated");
            } else {
                response.sendRedirect(request.getContextPath() + "/requestManagement?error=notfound");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/requestManagement?error=invalid");
        }
    }

    private void assignEmployeeToRequest(HttpServletRequest request, HttpServletResponse response, DataManager dm)
            throws IOException {

        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));
            String employeeUsername = request.getParameter("employeeUsername");

            Request req = dm.getRequest(requestId);
            if (req != null) {
                req.setAssignedTo(employeeUsername);
                req.setStatus("in_progress");
                dm.updateRequest(req);

                response.sendRedirect(request.getContextPath() + "/requestManagement?success=employee_assigned");
            } else {
                response.sendRedirect(request.getContextPath() + "/requestManagement?error=notfound");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/requestManagement?error=invalid");
        }
    }
}
