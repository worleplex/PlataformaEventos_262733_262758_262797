package com.example.EventYunks.controller;

import com.example.EventYunks.dto.EventoBorradorDTO;
import com.example.EventYunks.model.Evento;
import com.example.EventYunks.model.Organizador;
import com.example.EventYunks.model.enums.EstadoEvento;
import com.example.EventYunks.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@SessionAttributes("borrador")
@Controller
public class EventoOrganizadorController {

    private static final String VISTA_PANEL = "panel-organizador";
    private static final String REDIRECT_PANEL = "redirect:/organizador/eventos";
    private static final String VISTA_CREAR = "crear-evento";
    private static final String REDIRECT_PASO_DATOS = "redirect:/organizador/eventos/nuevo/datos";
    private static final String VISTA_RECINTO = "seleccionar-recinto";
    private static final String REDIRECT_PASO_RECINTO = "redirect:/organizador/eventos/nuevo/recinto";
    private static final String VISTA_ARTISTAS = "seleccionar-artistas";
    private static final String REDIRECT_PASO_ARTISTAS = "redirect:/organizador/eventos/nuevo/artistas";

    @Autowired
    private OrganizadorService organizadorService;
    @Autowired
    private EventoService eventoService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private RecintoService recintoService;
    @Autowired
    private ArtistaService artistaService;

    @GetMapping("/organizador/eventos")
    public String panelOrganizador(Model model) {
        Organizador organizador = organizadorService.obtenerOrganizadorActual();
        List<Evento> eventos = eventoService.eventosDelOrganizador(organizador.getId());
        model.addAttribute("eventos", eventos);
        model.addAttribute("totalEventos", eventos.size());
        model.addAttribute("boletosVendidos", ticketService.boletosVendidos(organizador.getId()));
        model.addAttribute("ingresosTotales", ticketService.ingresosTotales(organizador.getId()));
        return VISTA_PANEL;
    }

    @GetMapping("/organizador/eventos/{id}/estadisticas")
    public String estadisticas(@PathVariable Long id, Model model) {
        Organizador organizador = organizadorService.obtenerOrganizadorActual();
        Optional<Evento> evento = eventoService.detalleOrganizador(id, organizador.getId());
        if (evento.isEmpty()) {
            return REDIRECT_PANEL + "?error=eventoNoEncontrado";
        }
        model.addAttribute("evento", evento.get());
        model.addAttribute("asistentes", eventoService.asistentesDelEvento(evento.get()));
        return "estadisticas";
    }

    @ModelAttribute("borrador")
    public EventoBorradorDTO nuevoBorrador() {
        return new EventoBorradorDTO();
    }

    @InitBinder("borrador")
    public void configurarFormulario(WebDataBinder binder) {
        binder.setAllowedFields("nombre", "categoria", "descripcion", "fecha", "hora", "urlImagen", "boletos*");;
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping("/organizador/eventos/nuevo")
    public String nuevoEvento(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return REDIRECT_PASO_DATOS;
    }
    @GetMapping("/organizador/eventos/{id}/editar")
    public String editarEvento(@PathVariable Long id, Model model) {
        Organizador organizador = organizadorService.obtenerOrganizadorActual();
        Optional<EventoBorradorDTO> borrador = eventoService.borradorParaEditar(id, organizador.getId());
        if (borrador.isEmpty()) {
            return REDIRECT_PANEL + "?error=eventoNoEncontrado";
        }
        model.addAttribute("borrador", borrador.get());
        return REDIRECT_PASO_DATOS;
    }
    @GetMapping("/organizador/eventos/nuevo/datos")
    public String mostrarDatosGenerales(@ModelAttribute("borrador") EventoBorradorDTO borrador, Model model) {
        cargarResumen(model, borrador);
        return VISTA_CREAR;
    }
    @PostMapping("/organizador/eventos/nuevo/datos")
    public String guardarDatosGenerales(@ModelAttribute("borrador") EventoBorradorDTO borrador, @RequestParam("accion") String accion,
                                        @RequestParam(name = "imagen", required = false) MultipartFile imagen,
                                        Model model, SessionStatus sessionStatus, RedirectAttributes flash) {
        if (accion.equals("recinto")) {
            eventoService.guardarImagenBorrador(borrador, imagen);
            return REDIRECT_PASO_RECINTO;
        }
        boolean editando = borrador.getEventoId() != null;
        boolean accionValida = accion.equals("borrador") || accion.equals("publicar") || (accion.equals("guardar") && editando);
        if (!accionValida) {
            return REDIRECT_PASO_DATOS;
        }
        EstadoEvento estado = accion.equals("borrador") ? EstadoEvento.BORRADOR : accion.equals("publicar") ? EstadoEvento.PUBLICADO : null;
        boolean estricto = estado != EstadoEvento.BORRADOR;
        List<String> errores = eventoService.validarBorrador(borrador, estricto);
        if (errores.isEmpty()) {
            try {
                Organizador organizador = organizadorService.obtenerOrganizadorActual();
                if (editando) {
                    eventoService.actualizarEvento(borrador, organizador, estado, imagen);
                } else {
                    eventoService.crearEvento(borrador, organizador, estado, imagen);
                }
            } catch (IllegalArgumentException e) {
                errores = List.of(e.getMessage());
            }
        }
        if (!errores.isEmpty()) {
            model.addAttribute("errores", errores);
            cargarResumen(model, borrador);
            return VISTA_CREAR;
        }
        sessionStatus.setComplete();
        flash.addFlashAttribute("mensajeBien", editando
                ? "Los cambios del evento se guardaron correctamente"
                : (estado == EstadoEvento.PUBLICADO
                ? "El evento se publicó correctamente y ya está en venta"
                : "El evento se guardó como borrador"));
        return REDIRECT_PANEL;
    }

    private void cargarResumen(Model model, EventoBorradorDTO borrador) {
        if (borrador.getRecintoId() != null) {
            model.addAttribute("recintoSeleccionado", recintoService.buscarPorId(borrador.getRecintoId()).orElse(null));
        }
    }

    @GetMapping("/organizador/eventos/nuevo/recinto")
    public String mostrarRecinto(@ModelAttribute("borrador") EventoBorradorDTO borrador, Model model) {
        model.addAttribute("recintos", recintoService.listarTodos());
        if (borrador.getRecintoId() != null) {
            model.addAttribute("recintoSeleccionado", recintoService.buscarPorId(borrador.getRecintoId()).orElse(null));
        }
        return VISTA_RECINTO;
    }

    @PostMapping("/organizador/eventos/nuevo/recinto/seleccionar")
    public String seleccionarRecinto(@ModelAttribute("borrador") EventoBorradorDTO borrador, @RequestParam(name = "recintoId", required = false) Long recintoId) {
        if (recintoId != null && recintoService.buscarPorId(recintoId).isPresent()) {
            borrador.seleccionarRecinto(recintoId);
        }
        return REDIRECT_PASO_RECINTO;
    }

    @PostMapping("/organizador/eventos/nuevo/recinto")
    public String guardarBoletos(@ModelAttribute("borrador") EventoBorradorDTO borrador) {
        if (borrador.getRecintoId() == null) {
            return REDIRECT_PASO_RECINTO + "?error=sinRecinto";
        }

        return REDIRECT_PASO_ARTISTAS;
    }
    @GetMapping("/organizador/eventos/nuevo/artistas")
    public String mostrarArtistas(@ModelAttribute("borrador") EventoBorradorDTO borrador, @RequestParam(name = "buscar", required = false) String buscar, Model model) {
        model.addAttribute("artistas", artistaService.buscar(buscar));
        model.addAttribute("busquedaActual", buscar);
        return VISTA_ARTISTAS;
    }

    @PostMapping("/organizador/eventos/nuevo/artistas")
    public String guardarArtistas(@ModelAttribute("borrador") EventoBorradorDTO borrador, @RequestParam(name = "visibles", required = false) List<Long> visibles, @RequestParam(name = "artistaIds", required = false) List<Long> marcados, @RequestParam(name = "buscar", required = false) String buscar, @RequestParam("accion") String accion) {
        borrador.actualizarArtistas(visibles, marcados);
        if (accion.equals("buscar")) {
            if (buscar == null || buscar.isBlank()) {
                return REDIRECT_PASO_ARTISTAS;
            }
            return REDIRECT_PASO_ARTISTAS + "?buscar=" + URLEncoder.encode(buscar.trim(), StandardCharsets.UTF_8);
        }
        return REDIRECT_PASO_DATOS;
    }

    @GetMapping("/organizador/eventos/nuevo/cancelar")
    public String cancelarCreacion(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return REDIRECT_PANEL;
    }
}
