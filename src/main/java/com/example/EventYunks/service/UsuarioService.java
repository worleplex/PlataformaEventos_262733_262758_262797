package com.example.EventYunks.service;

import com.example.EventYunks.model.Usuario;
import com.example.EventYunks.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HttpSession session;

    public Usuario autenticarUsuario(String correo, String contrasenia) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreoIgnoreCase(correo.trim());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (Boolean.TRUE.equals(usuario.getActivo())
                    && passwordEncoder.matches(contrasenia, usuario.getHashContrasenia())) {
                return usuario;
            }
        }
        return null;
    }

    public Usuario obtenerUsuarioLogueado() {
        Long id = (Long) session.getAttribute("usuario_id");

        if (id != null) {
            Optional<Usuario> usuarioDet = this.usuarioRepository.findById(id);
            if (usuarioDet.isPresent()) {
                return usuarioDet.get();
            }
        }

        return null;
    }
}
