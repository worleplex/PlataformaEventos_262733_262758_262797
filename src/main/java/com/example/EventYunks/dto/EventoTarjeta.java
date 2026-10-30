package com.example.EventYunks.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventoTarjeta {

    private Long id;
    private String nombre;
    private String imagen;
    private String etiqueta;
    private String claseEtiqueta;
    private boolean cancelado;
    private String fecha;
    private String recinto;
    private String organizador;
    private String precio;
}
