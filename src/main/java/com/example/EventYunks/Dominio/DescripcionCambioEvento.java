package com.example.EventYunks.Dominio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cambios_descripcion")
@Getter
@Setter
@NoArgsConstructor
public class DescripcionCambioEvento extends CambioEvento {

    @Column(length = 2000)
    private String valorAnterior;

    @Column(length = 2000)
    private String valorNuevo;
}
