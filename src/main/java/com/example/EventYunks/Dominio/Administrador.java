package com.example.EventYunks.Dominio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "administradores")
@Getter
@Setter
@NoArgsConstructor
public class Administrador extends Usuario {

    @OneToMany(mappedBy = "creadoPor")
    private List<Organizador> organizadores = new ArrayList<>();

    @OneToMany(mappedBy = "administrador")
    private List<Artista> artistas = new ArrayList<>();

    @OneToMany(mappedBy = "administrador")
    private List<Recinto> recintos = new ArrayList<>();
}
