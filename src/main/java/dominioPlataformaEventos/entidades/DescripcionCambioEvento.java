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
public class DescripcionCambioEvento extends CambioEvento {

    private String valorAnterior;
    private String valorNuevo;

    public DescripcionCambioEvento(LocalDateTime fechaCambio, String valorAnterior, String valorNuevo) {
        super(TipoCambioEvento.DESCRIPCION, fechaCambio);
        this.valorAnterior = valorAnterior;
        this.valorNuevo = valorNuevo;
    }

    public String getValorAnterior() {
        return valorAnterior;
    }

    public void setValorAnterior(String valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public String getValorNuevo() {
        return valorNuevo;
    }

    public void setValorNuevo(String valorNuevo) {
        this.valorNuevo = valorNuevo;
    }
}