package com.example.EventYunks.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EventoDetalle {

    private Long id;
    private String nombre;
    private String categoria;
    private String descripcion;
    private String imagen;
    private String etiqueta;
    private String claseEtiqueta;
    private boolean cancelado;
    private String fechaLarga;
    private String recinto;
    private String direccion;
    private String organizadorNombre;
    private String organizadorCorreo;
    private boolean organizadorActivo;
    private long eventosRealizados;
    private List<ArtistaLinea> artistas;
    private List<BoletoLinea> boletos;
    private boolean puedeDesactivar;
    private boolean puedeReactivar;

    @Getter
    @AllArgsConstructor
    public static class ArtistaLinea {
        private String nombre;
        private String genero;
        private String imagen;
        private String iniciales;
    }

    @Getter
    @AllArgsConstructor
    public static class BoletoLinea {
        private String tipo;
        private String descripcion;
        private String precio;
        private String disponibilidad;
    }
}
