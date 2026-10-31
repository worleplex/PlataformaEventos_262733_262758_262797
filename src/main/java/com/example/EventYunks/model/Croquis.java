package com.example.EventYunks.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "croquis")
@Getter
@Setter
@NoArgsConstructor
public class Croquis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede pasar de 255 caracteres")
    @Column(nullable = false)
    private String nombre;

    @NotNull(message = "La capacidad es obligatoria")
    @Positive(message = "La capacidad debe ser un número mayor a 0")
    @Column(nullable = false)
    private Integer capacidad;

    @OneToMany(mappedBy = "croquis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Zona> zonas = new ArrayList<>();

    public void agregarZona(Zona zona) {
        zonas.add(zona);
        zona.setCroquis(this);
    }
}
