package com.example.EventYunks.Dominio;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recintos")
@Getter
@Setter
@NoArgsConstructor
public class Recinto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    @JoinColumn(name = "direccion_id", nullable = false)
    private Direccion direccion;

    @Positive
    private Integer capacidad;

    @Column(length = 500)
    private String croquis;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "administrador_id", nullable = false)
    private Administrador administrador;

    @OneToMany(mappedBy = "recinto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Zona> zonas = new ArrayList<>();

    @OneToMany(mappedBy = "recinto")
    private List<Evento> eventos = new ArrayList<>();

    public void agregarZona(Zona zona) {
        zonas.add(zona);
        zona.setRecinto(this);
    }
}
