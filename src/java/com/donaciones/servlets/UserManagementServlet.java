package com.donaciones.servlets;

import com.donaciones.utils.DataManager;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UserManagementServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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
        
        DataManager dm = DataManager.getInstance();
        String action = request.getParameter("action");
        
        if ("list".equals(action)) {
            request.setAttribute("allUsers", dm.getAllUsers());
            request.setAttribute("allDonors", dm.getAllDonors());
            request.setAttribute("allReceivers", dm.getAllReceivers());
            request.getRequestDispatcher("/WEB-INF/views/admin/user_management.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/userManagement?action=list");
        }
    }
}