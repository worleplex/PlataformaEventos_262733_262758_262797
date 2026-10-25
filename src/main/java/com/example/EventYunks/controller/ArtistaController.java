package com.example.EventYunks.controller;

import com.example.EventYunks.model.Artista;
import com.example.EventYunks.service.ArtistaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class ArtistaController {
    @Autowired
    private ArtistaService artistaService;


    @GetMapping("/admin/artistas")
    public String listarArtistas(@RequestParam(name = "buscar", required = false) String buscar, Model model) {
        if (buscar != null && !buscar.isEmpty()) {
            model.addAttribute("artistas", artistaService.buscar(buscar));
            model.addAttribute("busquedaActual", buscar);
        } else {
            model.addAttribute("artistas", artistaService.listarTodos());
        }
        return "gestion-artistas";
    }


    @GetMapping("/admin/artistas/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("artista", new Artista());
        return "crear-artista";
    }

    @PostMapping("/admin/artistas")
    public String guardarArtista(@RequestParam("nombre") String nombre,
                                 @RequestParam("generoMusical") String generoMusical,
                                 @RequestParam(name = "biografia", required = false) String biografia,
                                 @RequestParam(name = "imagen", required = false) MultipartFile imagen
                                 ) {
        Artista nuevo = new Artista();
        nuevo.setNombre(nombre);
        nuevo.setGeneroMusical(generoMusical);
        nuevo.setBiografia(biografia);
        //Falta recuperar al administrador del objeto de sesión (que aún no existe)
        artistaService.guardarArtista(nuevo, imagen);
        return "redirect:/admin/artistas";
    }


    @GetMapping("/admin/artistas/editar/{id}")
    public String editarArtista(@PathVariable Long id, Model model) {
        Artista artista = artistaService.obtenerArtistaPorId(id);

        if (artista != null) {
            model.addAttribute("artista", artista);
            return "editar-artista";
        }
        return "redirect:/admin/artistas";
    }

    @PostMapping("/admin/artistas/editar/{id}")
    public String actualizarArtista(@PathVariable Long id,
                                    @RequestParam("nombre") String nombre,
                                    @RequestParam("generoMusical") String generoMusical,
                                    @RequestParam(name = "biografia", required = false) String biografia,
                                    @RequestParam(name = "imagen", required = false) MultipartFile imagen) {
        Artista existente = artistaService.obtenerArtistaPorId(id);

        if (existente != null) {
            existente.setNombre(nombre);
            existente.setGeneroMusical(generoMusical);
            existente.setBiografia(biografia);

            artistaService.guardarArtista(existente, imagen);
        }
        return "redirect:/admin/artistas";
    }

    @PostMapping("/admin/artistas/eliminar/{id}")
    public String eliminarArtista(@PathVariable Long id) {
        boolean eliminado = artistaService.eliminarArtista(id);
        if(!eliminado){
            return "redirect:/admin/artistas?error=conEventos";
        }
        return "redirect:/admin/artistas";
    }
}
