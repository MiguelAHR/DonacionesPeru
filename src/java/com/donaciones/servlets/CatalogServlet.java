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

@WebServlet("/catalogo")
public class CatalogServlet extends HttpServlet {
    private DataManager dataManager;

    @Override
    public void init() throws ServletException {
        dataManager = DataManager.getInstance();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) action = "view";

        try {
            switch (action) {
                case "view":
                    List<Catalogo> catalogItems = dataManager.getAvailableCatalogItems();
                    request.setAttribute("catalogItems", catalogItems);
                    request.getRequestDispatcher("/catalogo/lista.jsp").forward(request, response);
                    break;
                    
                case "viewAll":
                    if (user.isAdmin() || user.isEmployee()) {
                        List<Catalogo> allItems = dataManager.getAllCatalogItems();
                        request.setAttribute("catalogItems", allItems);
                        request.getRequestDispatcher("/catalogo/listaCompleta.jsp").forward(request, response);
                    } else {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    }
                    break;
                    
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}