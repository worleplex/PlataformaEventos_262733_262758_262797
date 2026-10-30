package com.example.EventYunks.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario_id") == null) {
            response.sendRedirect("/login");
            return false;
        }

        String uri = request.getRequestURI();
        boolean rutaAdmin = uri.startsWith("/admin");
        if (rutaAdmin && !"ADMIN".equals(session.getAttribute("rol"))) {
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }
}
