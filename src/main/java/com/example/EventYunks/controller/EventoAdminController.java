package com.example.EventYunks.controller;

import com.example.EventYunks.dto.EventoDetalle;
import com.example.EventYunks.dto.EventoTarjeta;
import com.example.EventYunks.model.Organizador;
import com.example.EventYunks.service.EventoService;
import com.example.EventYunks.service.OrganizadorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/eventos")
public class EventoAdminController {

    private static final String VISTA_PANEL = "panel-administrador";
    private static final String VISTA_DETALLE = "evento-admin";
    private static final String REDIRECT_PANEL = "redirect:/admin/eventos";
    private static final String REDIRECT_DETALLE = "redirect:/admin/eventos/";

    private final EventoService eventoService;
    private final OrganizadorService organizadorService;

    public EventoAdminController(EventoService eventoService, OrganizadorService organizadorService) {
        this.eventoService = eventoService;
        this.organizadorService = organizadorService;
    }

    @GetMapping
    public String panel(@RequestParam(name = "buscar", required = false) String buscar,
                        @RequestParam(name = "categoria", required = false) String categoria,
                        @RequestParam(name = "fecha", required = false) String fecha,
                        @RequestParam(name = "precio", required = false) String precio,
                        Model model) {
        List<EventoTarjeta> eventos = eventoService.catalogoAdmin(buscar, categoria, fecha, precio);
        model.addAttribute("eventos", eventos);
        model.addAttribute("categorias", eventoService.categorias());
        model.addAttribute("busquedaActual", buscar);
        model.addAttribute("categoriaActual", categoria);
        model.addAttribute("fechaActual", fecha);
        model.addAttribute("precioActual", precio);
        model.addAttribute("hayFiltros", hayTexto(buscar) || hayTexto(categoria) || hayTexto(fecha) || hayTexto(precio));
        return VISTA_PANEL;
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model, RedirectAttributes flash) {
        Optional<EventoDetalle> detalle = eventoService.detalleAdmin(id);
        if (detalle.isEmpty()) {
            flash.addFlashAttribute("mensajeError", "El evento que intentas consultar ya no existe");
            return REDIRECT_PANEL;
        }
        model.addAttribute("evento", detalle.get());
        return VISTA_DETALLE;
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable Long id, RedirectAttributes flash) {
        if (eventoService.desactivar(id)) {
            flash.addFlashAttribute("mensajeBien", "El evento se desactivó correctamente y se suspendió la venta de boletos");
        } else {
            flash.addFlashAttribute("mensajeError", "No se pudo desactivar el evento en su estado actual");
        }
        return REDIRECT_DETALLE + id;
    }

    @PostMapping("/{id}/reactivar")
    public String reactivar(@PathVariable Long id, RedirectAttributes flash) {
        if (eventoService.reactivar(id)) {
            flash.addFlashAttribute("mensajeBien", "El evento se reactivó correctamente y vuelve a estar en venta");
        } else {
            flash.addFlashAttribute("mensajeError", "No se pudo reactivar el evento. Solo se pueden reactivar eventos cancelados que aún no ocurren");
        }
        return REDIRECT_DETALLE + id;
    }

    @PostMapping("/{id}/organizador/estado")
    public String cambiarEstadoOrganizador(@PathVariable Long id, RedirectAttributes flash) {
        Optional<Long> organizadorId = eventoService.idOrganizador(id);
        if (organizadorId.isEmpty()) {
            flash.addFlashAttribute("mensajeError", "El evento que intentas consultar ya no existe");
            return REDIRECT_PANEL;
        }
        organizadorService.cambiarEstadoCuenta(organizadorId.get());
        Organizador organizador = organizadorService.obtenerOrganizadorPorId(organizadorId.get());
        if (organizador != null) {
            String accion = Boolean.TRUE.equals(organizador.getActivo()) ? "se reactivó" : "se suspendió";
            flash.addFlashAttribute("mensajeBien", "La cuenta de " + organizador.getNombre() + " " + accion + " correctamente");
        }
        return REDIRECT_DETALLE + id;
    }

    private boolean hayTexto(String valor) {
        return valor != null && !valor.isBlank();
    }
}
