package com.example.EventYunks.controller;

import com.example.EventYunks.model.Direccion;
import com.example.EventYunks.model.Recinto;
import com.example.EventYunks.service.RecintoService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
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

    /**
     * Limita los campos que se pueden llenar desde el formulario, así nadie puede mandar por la petición
     * datos como el administrador, las zonas o el id del recinto
     */
    @InitBinder("recinto")
    public void limitarCampos(WebDataBinder binder) {
        binder.setAllowedFields("nombre", "capacidad",
                "direccion.nombreLugar", "direccion.calle", "direccion.numero",
                "direccion.colonia", "direccion.codigoPostal", "direccion.ciudad");
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
        Recinto recinto = new Recinto();
        recinto.setDireccion(new Direccion());
        model.addAttribute("recinto", recinto);
        return VISTA_CREAR;
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("recinto") Recinto recinto,
                        BindingResult resultado,
                        RedirectAttributes flash) {
        validarDuplicado(recinto, resultado);
        if (resultado.hasErrors()) {
            return VISTA_CREAR;
        }
        Recinto guardado = recintoService.crear(recinto);
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
        Recinto existente = recinto.get();
        if (existente.getDireccion() == null) {
            existente.setDireccion(new Direccion());
        }
        model.addAttribute("recinto", existente);
        return VISTA_EDITAR;
    }

    @PostMapping("/editar/{id}")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("recinto") Recinto recinto,
                             BindingResult resultado,
                             RedirectAttributes flash) {
        recinto.setId(id);
        if (recintoService.buscarPorId(id).isEmpty()) {
            flash.addFlashAttribute("mensajeError", "El recinto que intentas editar ya no existe");
            return REDIRECT_LISTA;
        }
        validarDuplicado(recinto, resultado);
        if (resultado.hasErrors()) {
            return VISTA_EDITAR;
        }
        Recinto guardado = recintoService.actualizar(id, recinto);
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

    private void validarDuplicado(Recinto recinto, BindingResult resultado) {
        if (!resultado.hasFieldErrors("nombre") && !resultado.hasFieldErrors("direccion.ciudad")
                && recintoService.existeDuplicado(recinto)) {
            resultado.rejectValue("nombre", "recinto.duplicado",
                    "Ya existe un recinto con ese nombre en esa ciudad");
        }
    }
}
