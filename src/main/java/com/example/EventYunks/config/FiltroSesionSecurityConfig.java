package com.example.EventYunks.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class FiltroSesionSecurityConfig extends OncePerRequestFilter {
    private static final Set<String> ROLES_SISTEMA = Set.of("ADMIN", "ORGANIZADOR", "ASISTENTE");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object usuarioId = session.getAttribute("usuario_id");
            Object rol = session.getAttribute("rol");
            if (usuarioId instanceof Long && rol instanceof String nombreRol && ROLES_SISTEMA.contains(nombreRol)) {
                SimpleGrantedAuthority autoridad = new SimpleGrantedAuthority("ROLE_" + nombreRol);
                UsernamePasswordAuthenticationToken autenticacion = new UsernamePasswordAuthenticationToken(usuarioId, null, List.of(autoridad));
                SecurityContext contexto = SecurityContextHolder.createEmptyContext();
                contexto.setAuthentication(autenticacion);
                SecurityContextHolder.setContext(contexto);
            }
        }
        filterChain.doFilter(request, response);
    }
}
