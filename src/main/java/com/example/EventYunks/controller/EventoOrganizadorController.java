package com.example.EventYunks.controller;

import com.example.EventYunks.model.Evento;
import com.example.EventYunks.model.Organizador;
import com.example.EventYunks.service.EventoService;
import com.example.EventYunks.service.OrganizadorService;
import com.example.EventYunks.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class EventoOrganizadorController {
    @Autowired
    private OrganizadorService organizadorService;
    @Autowired
    private EventoService eventoService;
    @Autowired
    private TicketService ticketService;

    @GetMapping("/organizador/eventos")
    public String panelOrganizador(Model model) {
        Organizador organizador = organizadorService.obtenerOrganizadorActual();
        List<Evento> eventos = eventoService.eventosDelOrganizador(organizador.getId());
        model.addAttribute("eventos", eventos);
        model.addAttribute("totalEventos", eventos.size());
        model.addAttribute("boletosVendidos", ticketService.boletosVendidos(organizador.getId()));
        model.addAttribute("ingresosTotales", ticketService.ingresosTotales(organizador.getId()));
        return "panel-organizador";
    }
}
