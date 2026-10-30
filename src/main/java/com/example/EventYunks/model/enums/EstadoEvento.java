package com.example.EventYunks.model.enums;

public enum EstadoEvento {
    BORRADOR("Borrador", ""),
    PUBLICADO("En Venta", ""),
    PAUSADO("Pausado", "espera"),
    CANCELADO("Cancelado", "cancelado"),
    FINALIZADO("Finalizado", "finalizado");

    private final String etiqueta;
    private final String claseEtiqueta;

    EstadoEvento(String etiqueta, String claseEtiqueta) {
        this.etiqueta = etiqueta;
        this.claseEtiqueta = claseEtiqueta;
    }

    /** @return el texto que se muestra en la etiqueta del evento */
    public String getEtiqueta() {
        return etiqueta;
    }

    /** @return la clase CSS que usa la etiqueta del evento */
    public String getClaseEtiqueta() {
        return claseEtiqueta;
    }
}
