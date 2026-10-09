package com.example.EventYunks.Dominio;

import com.example.EventYunks.Dominio.enums.EstadoAsiento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "asientos",
       uniqueConstraints = @UniqueConstraint(columnNames = {"zona_id", "fila", "numero"}))
@Getter
@Setter
@NoArgsConstructor
public class Asiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 10)
    private String fila;

    @NotNull
    @Column(nullable = false)
    private Integer numero;

    private Double posicionX;

    private Double posicionY;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAsiento estado = EstadoAsiento.DISPONIBLE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "zona_id", nullable = false)
    private Zona zona;
}
