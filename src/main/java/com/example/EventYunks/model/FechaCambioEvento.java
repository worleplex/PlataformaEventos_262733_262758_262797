package com.example.EventYunks.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "cambios_fecha")
@Getter
@Setter
@NoArgsConstructor
public class FechaCambioEvento extends CambioEvento {

    private LocalDateTime valorAnterior;

    private LocalDateTime valorNuevo;
}
