package com.example.EventYunks.controller;

import com.example.EventYunks.dto.RecintoForm;
import com.example.EventYunks.model.Recinto;
import com.example.EventYunks.service.RecintoService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/recintos")
public class RecintoController {

    private static final String VISTA_LISTA = "gestion-recintos";
    private static final String VISTA_CREAR = "crear-recinto";
    private static final String VISTA_EDITAR = "editar-recinto";
    private static final String REDIRECT_LISTA = "redirect:/admin/recintos";

    private final RecintoService recintoService;

    public RecintoController(RecintoService recintoService) {
        this.recintoService = recintoService;
    }

    @GetMapping
    public String listar(@RequestParam(name = "buscar", required = false) String buscar, Model model) {
        model.addAttribute("recintos", recintoService.buscar(buscar));
        model.addAttribute("totalRecintos", recintoService.contarTodos());
        model.addAttribute("busquedaActual", buscar);
        return VISTA_LISTA;
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("recintoForm", new RecintoForm());
        return VISTA_CREAR;
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("recintoForm") RecintoForm form,
                        BindingResult resultado,
                        RedirectAttributes flash) {
        form.setId(null);
        validarDuplicado(form, resultado);
        if (resultado.hasErrors()) {
            return VISTA_CREAR;
        }
        Recinto guardado = recintoService.guardar(form);
        flash.addFlashAttribute("mensajeBien", "El recinto " + guardado.getNombre() + " se creó correctamente");
        return REDIRECT_LISTA;
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model, RedirectAttributes flash) {
        Optional<Recinto> recinto = recintoService.buscarPorId(id);
        if (recinto.isEmpty()) {
            flash.addFlashAttribute("mensajeError", "El recinto que intentas editar ya no existe");
            return REDIRECT_LISTA;
        }
        model.addAttribute("recintoForm", RecintoForm.desde(recinto.get()));
        return VISTA_EDITAR;
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("recintoForm") RecintoForm form,
                             BindingResult resultado,
                             RedirectAttributes flash) {
        form.setId(id);
        if (recintoService.buscarPorId(id).isEmpty()) {
            flash.addFlashAttribute("mensajeError", "El recinto que intentas editar ya no existe");
            return REDIRECT_LISTA;
        }
        validarDuplicado(form, resultado);
        if (resultado.hasErrors()) {
            return VISTA_EDITAR;
        }
        Recinto guardado = recintoService.guardar(form);
        flash.addFlashAttribute("mensajeBien", "El recinto " + guardado.getNombre() + " se actualizó correctamente");
        return REDIRECT_LISTA;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        Optional<Recinto> recinto = recintoService.buscarPorId(id);
        if (recinto.isEmpty()) {
            flash.addFlashAttribute("mensajeError", "El recinto que intentas eliminar ya no existe");
            return REDIRECT_LISTA;
        }
        String nombre = recinto.get().getNombre();
        try {
            if (recintoService.eliminar(id)) {
                flash.addFlashAttribute("mensajeBien", "El recinto " + nombre + " se eliminó correctamente");
            } else {
                flash.addFlashAttribute("mensajeError",
                        "No se pudo eliminar el recinto " + nombre + " porque tiene eventos asociados");
            }
        } catch (DataIntegrityViolationException e) {
            flash.addFlashAttribute("mensajeError",
                    "No se pudo eliminar el recinto " + nombre + " porque otros registros dependen de él");
        }
        return REDIRECT_LISTA;
    }

    private void validarDuplicado(RecintoForm form, BindingResult resultado) {
        if (!resultado.hasFieldErrors("nombre") && !resultado.hasFieldErrors("ciudad")
                && recintoService.existeDuplicado(form)) {
            resultado.rejectValue("nombre", "recinto.duplicado",
                    "Ya existe un recinto con ese nombre en esa ciudad");
        }
    }
}
