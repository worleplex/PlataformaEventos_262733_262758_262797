package dominioPlataformaEventos.entidades;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import dominioPlataformaEventos.enums.TipoCambioEvento;
import java.time.LocalDateTime;

/**
 *
 * @author julian izaguirre, Erick Armenta, Willian Tolano
 */
public class CambioEvento {
    private Long id;
    private TipoCambioEvento campoModificado;
    private LocalDateTime fechaCambio;
    private Evento evento;
    private Organizador organizador;
    
    protected CambioEvento(TipoCambioEvento campoModificado, LocalDateTime fechaCambio) {
        this.campoModificado = campoModificado;
        this.fechaCambio = fechaCambio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoCambioEvento getCampoModificado() {
        return campoModificado;
    }

    public void setCampoModificado(TipoCambioEvento campoModificado) {
        this.campoModificado = campoModificado;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public Organizador getOrganizador() {
        return organizador;
    }

    public void setOrganizador(Organizador organizador) {
        this.organizador = organizador;
    }
}
