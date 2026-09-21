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
public class FechaCambioEvento extends CambioEvento {

    private LocalDateTime valorAnterior;
    private LocalDateTime valorNuevo;

    public FechaCambioEvento(LocalDateTime fechaCambio, LocalDateTime valorAnterior, LocalDateTime valorNuevo) {
        super(TipoCambioEvento.FECHA, fechaCambio);
        this.valorAnterior = valorAnterior;
        this.valorNuevo = valorNuevo;
    }

    public LocalDateTime getValorAnterior() {
        return valorAnterior;
    }

    public void setValorAnterior(LocalDateTime valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public LocalDateTime getValorNuevo() {
        return valorNuevo;
    }

    public void setValorNuevo(LocalDateTime valorNuevo) {
        this.valorNuevo = valorNuevo;
    }
}

