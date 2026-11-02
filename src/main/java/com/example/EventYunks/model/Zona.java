package com.example.EventYunks.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "zonas")
@Getter
@Setter
@NoArgsConstructor
public class Zona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "croquis_id", nullable = false)
    private Croquis croquis;

    @OneToMany(mappedBy = "zona", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asiento> asientos = new ArrayList<>();

    public void agregarAsiento(Asiento asiento) {
        asientos.add(asiento);
        asiento.setZona(this);
    }

    public Map<String, List<Asiento>> getAsientosPorFila() {
        Map<String, List<Asiento>> filas = new TreeMap<>(Comparator.comparing(String::length).thenComparing(Comparator.naturalOrder()));
        for (Asiento asiento : asientos) {
            filas.computeIfAbsent(asiento.getFila(), f -> new ArrayList<>()).add(asiento);
        }
        filas.values().forEach(lista -> lista.sort(Comparator.comparing(Asiento::getNumero)));
        return filas;
    }
}
