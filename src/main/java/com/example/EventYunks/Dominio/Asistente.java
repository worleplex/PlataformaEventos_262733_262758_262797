package com.example.EventYunks.Dominio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "asistentes")
@Getter
@Setter
@NoArgsConstructor
public class Asistente extends Usuario {

    @OneToMany(mappedBy = "asistente")
    private List<Compra> compras = new ArrayList<>();
}
