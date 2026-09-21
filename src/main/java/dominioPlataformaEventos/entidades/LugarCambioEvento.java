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
public class LugarCambioEvento extends CambioEvento {

    private Direccion valorAnterior;
    private Direccion valorNuevo;

    public LugarCambioEvento(LocalDateTime fechaCambio, Direccion valorAnterior, Direccion valorNuevo) {
        super(TipoCambioEvento.LUGAR, fechaCambio);
        this.valorAnterior = valorAnterior;
        this.valorNuevo = valorNuevo;
    }

    public Direccion getValorAnterior() {
        return valorAnterior;
    }

    public void setValorAnterior(Direccion valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public Direccion getValorNuevo() {
        return valorNuevo;
    }

    public void setValorNuevo(Direccion valorNuevo) {
        this.valorNuevo = valorNuevo;
    }
}