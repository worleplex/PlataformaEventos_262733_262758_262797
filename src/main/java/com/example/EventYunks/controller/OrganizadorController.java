package com.example.EventYunks.controller;

import com.example.EventYunks.model.Organizador;
import com.example.EventYunks.service.OrganizadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OrganizadorController {

    @Autowired
    private OrganizadorService organizadorService;

    @GetMapping("/admin/organizadores")
    public String listarOrganizadores(@RequestParam(name = "buscar", required = false) String buscar, Model model) {
        if (buscar != null && !buscar.isEmpty()) {
            model.addAttribute("organizadores", organizadorService.buscar(buscar));
            model.addAttribute("busquedaActual", buscar);
        } else {
            model.addAttribute("organizadores", organizadorService.listarTodos());
        }
        return "gestion-organizadores";
    }

    @GetMapping("/admin/organizadores/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("organizador", new Organizador());
        return "crear-organizador";
    }

    @PostMapping("/admin/organizadores")
    public String guardarOrganizador(@RequestParam("nombre") String nombre,
                                     @RequestParam("correo") String correo,
                                     @RequestParam(name = "telefono", required = false) String telefono,
                                     @RequestParam("contrasenia") String contrasenia,
                                     Model model) {
        Organizador nuevo = new Organizador();
        nuevo.setNombre(nombre.trim());
        nuevo.setCorreo(correo.trim());
        nuevo.setTelefono(telefono);

        if (organizadorService.correoEnUso(nuevo.getCorreo(), null)) {
            model.addAttribute("organizador", nuevo);
            model.addAttribute("errorCorreo", "Ya existe una cuenta registrada con ese correo.");
            return "crear-organizador";
        }
        organizadorService.guardarOrganizador(nuevo, contrasenia);
        return "redirect:/admin/organizadores";
    }

    @GetMapping("/admin/organizadores/editar/{id}")
    public String editarOrganizador(@PathVariable Long id, Model model) {
        Organizador organizador = organizadorService.obtenerOrganizadorPorId(id);

        if (organizador != null) {
            model.addAttribute("organizador", organizador);
            return "editar-organizador";
        }
        return "redirect:/admin/organizadores";
    }

    @PostMapping("/admin/organizadores/editar/{id}")
    public String actualizarOrganizador(@PathVariable Long id,
                                        @RequestParam("nombre") String nombre,
                                        @RequestParam("correo") String correo,
                                        @RequestParam(name = "telefono", required = false) String telefono,
                                        @RequestParam(name = "contrasenia", required = false) String contrasenia,
                                        Model model) {
        Organizador existente = organizadorService.obtenerOrganizadorPorId(id);

        if (existente != null) {
            if (organizadorService.correoEnUso(correo.trim(), id)) {
                existente.setNombre(nombre.trim());
                existente.setCorreo(correo.trim());
                existente.setTelefono(telefono);
                model.addAttribute("organizador", existente);
                model.addAttribute("errorCorreo", "Ya existe una cuenta registrada con ese correo.");
                return "editar-organizador";
            }
            existente.setNombre(nombre.trim());
            existente.setCorreo(correo.trim());
            existente.setTelefono(telefono);

            organizadorService.guardarOrganizador(existente, contrasenia);
        }
        return "redirect:/admin/organizadores";
    }

    @PostMapping("/admin/organizadores/eliminar/{id}")
    public String eliminarOrganizador(@PathVariable Long id) {
        boolean eliminado = organizadorService.eliminarOrganizador(id);
        if (!eliminado) {
            return "redirect:/admin/organizadores?error=conEventos";
        }
        return "redirect:/admin/organizadores";
    }

    @PostMapping("/admin/organizadores/estado/{id}")
    public String cambiarEstadoCuenta(@PathVariable Long id) {
        organizadorService.cambiarEstadoCuenta(id);
        return "redirect:/admin/organizadores";
    }
}
