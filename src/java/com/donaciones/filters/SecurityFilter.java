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

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        String queryString = httpRequest.getQueryString();
        String method = httpRequest.getMethod();

        System.out.println("SecurityFilter - Method: " + method + ", Path: " + path + ", Query: " + queryString);

        // URLs públicas que no requieren autenticación
        if (isPublicResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Verificar sesión para URLs protegidas
        if (session == null || session.getAttribute("username") == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        String userType = (String) session.getAttribute("userType");
        System.out.println("SecurityFilter - UserType: " + userType);

        // Control de acceso por roles - CORREGIDO PARA USUARIOS
        if (!hasAccess(userType, path, queryString, method)) {
            System.out.println("SecurityFilter - Acceso denegado para: " + path);
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso denegado");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicResource(String path) {
        return path.startsWith("/login")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.equals("/")
                || path.equals("/index.html")
                || path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".jpg")
                || path.endsWith(".png")
                || path.endsWith(".ico");
    }

    private boolean hasAccess(String userType, String path, String queryString, String method) {
    if (userType == null) {
        return false;
    }

    // URLs que todos los usuarios autenticados pueden acceder
    if (path.equals("/logout")
            || path.equals("/dashboard")
            || path.equals("/profile")) {
        return true;
    }

    switch (userType.toLowerCase()) {
        case "admin":
            return true;

        case "empleado":
            if (path.equals("/reports")
                    || path.equals("/donations")
                    || path.equals("/users")
                    || path.startsWith("/empleado/")) {
                return true;
            }
            return !path.startsWith("/admin/")
                    && !path.equals("/userManagement")
                    && !path.equals("/donationManagement");

        case "usuario":
            // Usuario puede acceder a sus donaciones y solicitudes - CORREGIDO
            if (path.equals("/donations")) {
                if ("GET".equals(method)) {
                    // Permitir con cualquier parámetro de query string
                    return queryString == null 
                        || queryString.contains("action=myDonations")
                        || queryString.contains("action=myRequests") 
                        || queryString.contains("action=new")
                        || queryString.contains("action=newRequest")
                        || queryString.contains("action=list")
                        || queryString.contains("success=")
                        || queryString.contains("error=");
                }
                // Puede enviar el formulario de donación/solicitud
                if ("POST".equals(method)) {
                    return true;
                }
            }
            // Puede acceder a su perfil
            if (path.startsWith("/usuario/")) {
                return true;
            }
            return false;
        default:
            return false;
    }
}

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
