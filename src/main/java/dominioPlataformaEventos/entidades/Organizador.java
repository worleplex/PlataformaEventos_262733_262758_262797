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
public class Organizador extends Usuario {
    private List<Evento> eventos = new ArrayList<>();
    private List<CambioEvento> cambiosRealizados = new ArrayList<>();

    public Organizador(String nombre, String correo, String hashContrasenia, Boolean activo) {
        super(nombre, correo, hashContrasenia, TipoUsuario.ORGANIZADOR, activo);
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public void agregarEvento(Evento evento) {
        eventos.add(evento);
    }

    public List<CambioEvento> getCambiosRealizados() {
        return cambiosRealizados;
    }

    public void registrarCambio(CambioEvento cambio) {
        cambiosRealizados.add(cambio);
        cambio.setOrganizador(this);
    }
}
