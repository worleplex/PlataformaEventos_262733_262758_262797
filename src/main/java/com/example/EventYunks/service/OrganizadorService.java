package com.example.EventYunks.service;

import com.example.EventYunks.model.Organizador;
import com.example.EventYunks.repository.OrganizadorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizadorService {

    private final OrganizadorRepository organizadorRepository;
    private final AdministradorService administradorService;
    // Es el mismo bean BCrypt que define SecurityConfig, así el hash se genera igual en todo el proyecto
    private final PasswordEncoder passwordEncoder;

    public OrganizadorService(OrganizadorRepository organizadorRepository,
                              AdministradorService administradorService,
                              PasswordEncoder passwordEncoder) {
        this.organizadorRepository = organizadorRepository;
        this.administradorService = administradorService;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Organizador> listarTodos() {
        return organizadorRepository.findAllByOrderByNombreAsc();
    }

    public List<Organizador> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return listarTodos();
        }
        return organizadorRepository.buscarPorNombreOCorreo(texto.trim());
    }

    public Organizador obtenerOrganizadorPorId(Long id) {
        return organizadorRepository.findById(id).orElse(null);
    }

    public boolean correoEnUso(String correo, Long idExcluido) {
        if (idExcluido == null) {
            return organizadorRepository.existsByCorreoIgnoreCase(correo);
        }
        return organizadorRepository.existsByCorreoIgnoreCaseAndIdNot(correo, idExcluido);
    }

    /**
     * Guarda un organizador. Si viene contraseña se cifra con BCrypt (al editar, si viene vacía se conserva la anterior).
     * Si es nuevo, se registra como creado por el administrador que tiene la sesión activa.
     */
    public void guardarOrganizador(Organizador organizador, String contrasenia) {
        if (contrasenia != null && !contrasenia.isBlank()) {
            organizador.setHashContrasenia(passwordEncoder.encode(contrasenia));
        }
        if (organizador.getCreadoPor() == null) {
            organizador.setCreadoPor(administradorService.obtenerAdministradorActual());
        }
        organizadorRepository.save(organizador);
    }

    public boolean eliminarOrganizador(Long id) {
        Organizador organizador = organizadorRepository.findById(id).orElse(null);
        if (organizador != null) {
            if (!organizador.getEventos().isEmpty() || !organizador.getCambios().isEmpty()) {
                return false;
            }
            organizadorRepository.deleteById(id);
        }
        return true;
    }

    public void cambiarEstadoCuenta(Long id) {
        Organizador organizador = organizadorRepository.findById(id).orElse(null);
        if (organizador != null) {
            organizador.setActivo(!Boolean.TRUE.equals(organizador.getActivo()));
            organizadorRepository.save(organizador);
        }
    }
}
