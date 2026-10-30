package com.example.EventYunks.service;

import com.example.EventYunks.model.Administrador;
import com.example.EventYunks.repository.AdministradorRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdministradorDetailsService implements UserDetailsService {

    private final AdministradorRepository administradorRepository;

    public AdministradorDetailsService(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        String correoLimpio = correo == null ? "" : correo.trim();

        Administrador administrador = administradorRepository.findByCorreoIgnoreCase(correoLimpio)
                .orElseThrow(() -> new UsernameNotFoundException("credenciales invalidas"));

        return User.withUsername(administrador.getCorreo())
                .password(administrador.getHashContrasenia())
                .roles("ADMIN")
                .disabled(!Boolean.TRUE.equals(administrador.getActivo()))
                .build();
    }
}
