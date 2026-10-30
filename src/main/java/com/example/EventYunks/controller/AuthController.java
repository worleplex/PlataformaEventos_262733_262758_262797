package com.example.EventYunks.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/admin/login")
    public String mostrarLogin(Authentication autenticacion) {
        if (autenticacion != null && autenticacion.isAuthenticated()
                && !(autenticacion instanceof AnonymousAuthenticationToken)) {
            return "redirect:/admin/organizadores";
        }
        return "login";
    }

    @GetMapping("/admin")
    public String inicioAdmin() {
        return "redirect:/admin/organizadores";
    }
}
