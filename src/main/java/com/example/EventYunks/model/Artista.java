package com.example.EventYunks.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "artistas")
@Getter
@Setter
@NoArgsConstructor
public class Artista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    private String generoMusical;

    @Column(length = 2000)
    private String biografia;

    @Column(length = 500)
    private String urlImagen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "administrador_id", nullable = false)
    private Administrador administrador;

    @ManyToMany(mappedBy = "artistas")
    private List<Evento> eventos = new ArrayList<>();

    /**
     * Iniciales del artista (primera letra de las dos primeras palabras), para mostrar cuando no tiene imagen
     * @return las iniciales en mayúsculas
     */
    public String getIniciales() {
        String limpio = nombre == null ? "" : nombre.trim();
        if (limpio.isEmpty()) {
            return "?";
        }
        String[] palabras = limpio.split("\\s+");
        String resultado = palabras[0].substring(0, 1);
        if (palabras.length > 1) {
            resultado += palabras[1].substring(0, 1);
        }
        return resultado.toUpperCase();
    }
}
