package com.example.EventYunks.service;

import com.example.EventYunks.model.Administrador;
import com.example.EventYunks.model.Organizador;
import com.example.EventYunks.repository.AdministradorRepository;
import com.example.EventYunks.repository.OrganizadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizadorService {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private OrganizadorRepository organizadorRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

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

    public void guardarOrganizador(Organizador organizador, String contrasenia) {
        if (contrasenia != null && !contrasenia.isBlank()) {
            organizador.setHashContrasenia(passwordEncoder.encode(contrasenia));
        }
        if (organizador.getCreadoPor() == null) {
            organizador.setCreadoPor(obtenerAdministradorActual());
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

    private Administrador obtenerAdministradorActual() {
        return administradorRepository.findFirstByOrderByIdAsc().orElseThrow(() -> new IllegalStateException("No hay ningún administrador en la base de datos."));
    }
}
