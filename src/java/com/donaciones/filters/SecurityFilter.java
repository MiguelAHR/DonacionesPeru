package com.donaciones.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SecurityFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        System.out.println("DEBUG SecurityFilter - URI: " + requestURI);
        
        // URLs públicas que no requieren autenticación
        boolean isPublicResource = requestURI.endsWith("login.jsp") ||
                                  requestURI.contains("/login") ||
                                  requestURI.contains("/css/") ||
                                  requestURI.contains("/js/") ||
                                  requestURI.contains("/images/") ||
                                  requestURI.contains("/webfonts/") ||
                                  requestURI.endsWith(".html") ||
                                  requestURI.endsWith(".css") ||
                                  requestURI.endsWith(".js") ||
                                  requestURI.endsWith(".png") ||
                                  requestURI.endsWith(".jpg");

        if (isPublicResource) {
            chain.doFilter(request, response);
            return;
        }

        // Verificar si el usuario está logueado
        boolean isLoggedIn = (session != null && session.getAttribute("username") != null);

        if (!isLoggedIn) {
            System.out.println("DEBUG SecurityFilter - Usuario no autenticado, redirigiendo a login");
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }

        // Usuario autenticado, continuar
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialización si es necesaria
    }

    @Override
    public void destroy() {
        // Cleanup si es necesario
    }
}