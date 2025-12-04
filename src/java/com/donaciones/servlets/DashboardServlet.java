package com.donaciones.servlets;

import com.donaciones.models.User;
import com.donaciones.utils.DataManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class DashboardServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        System.out.println("🚀 DEBUG DashboardServlet - Iniciando");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            System.out.println("❌ DEBUG DashboardServlet - No hay sesión, redirigiendo a login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // **CRÍTICO: Sincronizar imagen de perfil al cargar el dashboard**
        String username = (String) session.getAttribute("username");
        DataManager dm = DataManager.getInstance();
        User user = dm.getUser(username);
        
        if (user != null) {
            String profileImage = user.getProfileImage();
            
            // Normalizar la ruta
            if (profileImage != null && !profileImage.isEmpty() && !profileImage.equals("null")) {
                if (!profileImage.startsWith("http") && !profileImage.startsWith(request.getContextPath())) {
                    if (profileImage.startsWith("/")) {
                        profileImage = request.getContextPath() + profileImage;
                    } else {
                        profileImage = request.getContextPath() + "/" + profileImage;
                    }
                }
                session.setAttribute("profileImage", profileImage);
                System.out.println("✅ DEBUG DashboardServlet - Imagen sincronizada: " + profileImage);
            }
        }
        
        // Redirigir al ProfileServlet que maneja todo
        System.out.println("↪️ DEBUG DashboardServlet - Redirigiendo a /profile");
        response.sendRedirect(request.getContextPath() + "/profile");
    }
}