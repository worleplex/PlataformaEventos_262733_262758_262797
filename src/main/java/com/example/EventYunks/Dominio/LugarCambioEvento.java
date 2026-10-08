package com.example.EventYunks.Dominio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cambios_lugar")
@Getter
@Setter
@NoArgsConstructor
public class LugarCambioEvento extends CambioEvento {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recinto_anterior_id", nullable = false)
    private Recinto valorAnterior;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recinto_nuevo_id", nullable = false)
    private Recinto valorNuevo;
}
