package dominioPlataformaEventos.entidades;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author julian izaguirre, Erick Armenta, Willian Tolano
 */
public class Boleto {
    private Long id;
    private String tipo;
    private Double precio;
    private Integer cantidadDisponibles;
    private Integer cantidadTotal;
    private String descripcion;
    private Evento evento;
    private List<Ticket> tickets = new ArrayList<>();
    
    public Boleto(String tipo, Double precio, Integer cantidadDisponibles, Integer cantidadTotal, String descripcion) {
        this.tipo = tipo;
        this.precio = precio;
        this.cantidadDisponibles = cantidadDisponibles;
        this.cantidadTotal = cantidadTotal;
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getCantidadDisponibles() {
        return cantidadDisponibles;
    }

    public void setCantidadDisponibles(Integer cantidadDisponibles) {
        this.cantidadDisponibles = cantidadDisponibles;
    }
    
    public Integer getCantidadTotal() {
        return cantidadTotal;
    }

    public void setCantidadTotal(Integer cantidadTotal) {
        this.cantidadTotal = cantidadTotal;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void agregarTicket(Ticket ticket) {
        tickets.add(ticket);
        ticket.setBoleto(this);
    }
}
