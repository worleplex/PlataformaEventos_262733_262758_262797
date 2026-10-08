package com.example.EventYunks.Dominio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organizadores")
@Getter
@Setter
@NoArgsConstructor
public class Organizador extends Usuario {

    @OneToMany(mappedBy = "organizador")
    private List<Evento> eventos = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrador_id")
    private Administrador creadoPor;

    @OneToMany(mappedBy = "organizador")
    private List<CambioEvento> cambios = new ArrayList<>();
}
