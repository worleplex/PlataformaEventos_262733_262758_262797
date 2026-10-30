package com.example.EventYunks.service;

import com.example.EventYunks.model.Administrador;
import com.example.EventYunks.repository.AdministradorRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class AdministradorService {

    private final AdministradorRepository administradorRepository;
    private final HttpSession session;

    public AdministradorService(AdministradorRepository administradorRepository, HttpSession session) {
        this.administradorRepository = administradorRepository;
        this.session = session;
    }

    /**
     * Método que regresa el administrador de la sesión actual
     * @return el administrador autenticado
     * @throws IllegalStateException si no hay sesión de administrador
     */
    public Administrador obtenerAdministradorActual() {
        Long id = (Long) session.getAttribute("usuario_id");

        if (id == null) {
            throw new IllegalStateException("no hay una sesion de administrador activa");
        }

        return administradorRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("el administrador de la sesion ya no existe en la BD"));
    }
}
