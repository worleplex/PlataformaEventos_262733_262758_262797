package dominioPlataformaEventos.entidades;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import dominioPlataformaEventos.enums.TipoUsuario;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author julian izaguirre, Erick Armenta, Willian Tolano
 */
public class Asistente extends Usuario {
    
    private List<Ticket> tickets = new ArrayList<>();
    
    public Asistente(String nombre, String correo, String hashContrasenia, Boolean activo) {
        super(nombre, correo, hashContrasenia, TipoUsuario.ORGANIZADOR, activo);
    }
    
    public List<Ticket> getTickets() {
        return tickets;
    }
    
    public void agregarTicket(Ticket ticket) {
        tickets.add(ticket);
        ticket.setAsistente(this);
    }
}
