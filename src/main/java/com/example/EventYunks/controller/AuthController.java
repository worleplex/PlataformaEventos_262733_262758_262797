package com.example.EventYunks.controller;

import com.example.EventYunks.model.Administrador;
import com.example.EventYunks.model.Organizador;
import com.example.EventYunks.model.Usuario;
import com.example.EventYunks.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping({"/", "/login"})
    public String mostrarLogin() {
        return "index";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam("correo") String correo,
                                @RequestParam("contrasenia") String contrasenia,
                                Model model,
                                HttpSession session) {

        Usuario usuario = usuarioService.autenticarUsuario(correo, contrasenia);

        if (usuario != null) {
            session.setAttribute("usuario_id", usuario.getId());
            if (usuario instanceof Administrador) {
                session.setAttribute("rol", "ADMIN");
                return "redirect:/admin/eventos";
            }
            if (usuario instanceof Organizador) {
                session.setAttribute("rol", "ORGANIZADOR");
                return "redirect:/panel-organizador.html";
            }
            session.setAttribute("rol", "ASISTENTE");
            return "redirect:/catalogo.html";
        } else {
            model.addAttribute("error", "Usuario Incorrecto");
            return "index";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/index.html")
    public String cerrarSesionPantallasEstaticas() {
        return "redirect:/logout";
    }

    @GetMapping("/admin")
    public String inicioAdmin() {
        return "redirect:/admin/eventos";
    }
}
