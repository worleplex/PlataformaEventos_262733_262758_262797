package com.example.EventYunks.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Lo utilice por mientras para mapear todas las clases que en realidad son estaticas
 * pero estan en templates para usar el layout base
 */
@Controller
public class StaticController {

    @GetMapping("/catalogo.html")
    public String catalogo() {
        return "catalogo";
    }

    @GetMapping("/evento.html")
    public String evento() {
        return "evento";
    }

    @GetMapping("/elegir-asientos.html")
    public String elegirAsientos() {
        return "elegir-asientos";
    }

    @GetMapping("/resumen-compra.html")
    public String resumenCompra() {
        return "resumen-compra";
    }

    @GetMapping("/pago-pendiente.html")
    public String pagoPendiente() {
        return "pago-pendiente";
    }

    @GetMapping("/pago-aprobado.html")
    public String pagoAprobado() {
        return "pago-aprobado";
    }

    @GetMapping("/pago-error.html")
    public String pagoError() {
        return "pago-error";
    }

    @GetMapping("/compra-completada.html")
    public String compraCompletada() {
        return "compra-completada";
    }

    @GetMapping("/mis-tickets.html")
    public String misTickets() {
        return "mis-tickets";
    }

    @GetMapping("/evento-cancelado.html")
    public String eventoCancelado() {
        return "evento-cancelado";
    }

    @GetMapping("/evento-finalizado.html")
    public String eventoFinalizado() {
        return "evento-finalizado";
    }

    @GetMapping("/panel-organizador.html")
    public String panelOrganizador() {
        return "panel-organizador";
    }

    @GetMapping("/crear-evento.html")
    public String crearEvento() {
        return "crear-evento";
    }

    @GetMapping("/editar-evento.html")
    public String editarEvento() {
        return "editar-evento";
    }

    @GetMapping("/seleccionar-recinto.html")
    public String seleccionarRecinto() {
        return "seleccionar-recinto";
    }

    @GetMapping("/seleccionar-artistas.html")
    public String seleccionarArtistas() {
        return "seleccionar-artistas";
    }

    @GetMapping("/asignar-asientos.html")
    public String asignarAsientos() {
        return "asignar-asientos";
    }

    @GetMapping("/estadisticas.html")
    public String estadisticas() {
        return "estadisticas";
    }

    @GetMapping({"/registro", "/registro.html"})
    public String registro() {
        return "registro";
    }
}