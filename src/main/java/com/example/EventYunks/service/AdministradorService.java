package com.example.EventYunks.service;

import com.example.EventYunks.model.Administrador;
import com.example.EventYunks.repository.AdministradorRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AdministradorService {

    private final AdministradorRepository administradorRepository;

    public AdministradorService(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    /**
     * Método que regresa el administrador de la sesión actual
     * @return el administrador autenticado
     * @throws IllegalStateException si no hay sesión de administrador
     */
    public Administrador obtenerAdministradorActual() {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();

        if (autenticacion == null || !autenticacion.isAuthenticated()
                || autenticacion instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("no hay una sesion de administrador activa");
        }

        return administradorRepository.findByCorreoIgnoreCase(autenticacion.getName())
                .orElseThrow(() -> new IllegalStateException("el administrador de la sesion ya no existe en la BD"));
    }
}
