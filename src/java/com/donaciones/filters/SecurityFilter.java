package com.donaciones.filters;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;

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

        // Recursos públicos
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

        // Verificar sesión
        boolean isLoggedIn = (session != null && session.getAttribute("username") != null);

        if (!isLoggedIn) {
            System.out.println("DEBUG SecurityFilter - Usuario no autenticado, redirigiendo a login");
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }

        // Control de permisos por rol
        String rol = (String) session.getAttribute("rol");

        if (requestURI.contains("/admin/") && !"admin".equalsIgnoreCase(rol)) {
            System.out.println("DEBUG SecurityFilter - Acceso denegado a zona admin");
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Usuario autenticado y autorizado
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void destroy() { }
}