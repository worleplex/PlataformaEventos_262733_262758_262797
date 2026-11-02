package com.example.EventYunks.dto;

import com.example.EventYunks.model.Artista;
import com.example.EventYunks.model.Boleto;
import com.example.EventYunks.model.Evento;
import com.example.EventYunks.model.enums.EstadoEvento;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class EventoBorradorDTO implements Serializable {

    private String nombre;
    private String categoria;
    private String descripcion;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime hora;

    private String urlImagen;

    private Long recintoId;

    private List<Long> artistaIds = new ArrayList<>();

    private List<BoletoBorradorDTO> boletos = new ArrayList<>();

    private Long eventoId;

    private EstadoEvento estadoEvento;

    public void seleccionarRecinto(Long nuevoRecintoId) {
        if (!Objects.equals(this.recintoId, nuevoRecintoId)) {
            this.recintoId = nuevoRecintoId;
            this.boletos = new ArrayList<>();
        }
    }
    public void actualizarArtistas(List<Long> visibles, List<Long> marcados) {
        if (visibles == null) {
            return;
        }
        artistaIds.removeAll(visibles);
        if (marcados != null) {
            for (Long id : marcados) {
                if (visibles.contains(id) && !artistaIds.contains(id)) {
                    artistaIds.add(id);
                }
            }
        }
    }

    public static EventoBorradorDTO desde(Evento evento) {
        EventoBorradorDTO borrador = new EventoBorradorDTO();
        borrador.eventoId = evento.getId();
        borrador.estadoEvento = evento.getEstado();
        borrador.nombre = evento.getNombre();
        borrador.categoria = evento.getCategoria();
        borrador.descripcion = evento.getDescripcion();
        borrador.fecha = evento.getFecha().toLocalDate();
        borrador.hora = evento.getFecha().toLocalTime();
        borrador.urlImagen = evento.getUrlImagen();
        borrador.recintoId = evento.getRecinto().getId();
        evento.getBoletos().stream().sorted(Comparator.comparing(Boleto::getId))
                .forEach(boleto -> {
                    BoletoBorradorDTO fila = new BoletoBorradorDTO();
                    fila.setBoletoId(boleto.getId());
                    fila.setTipo(boleto.getTipo());
                    fila.setPrecio(boleto.getPrecio());
                    fila.setCantidad(boleto.getCantidadTotal());
                    fila.setDescripcion(boleto.getDescripcion());
                    borrador.boletos.add(fila);
                });
        for (Artista artista : evento.getArtistas()) {
            borrador.artistaIds.add(artista.getId());
        }
        return borrador;
    }
}
