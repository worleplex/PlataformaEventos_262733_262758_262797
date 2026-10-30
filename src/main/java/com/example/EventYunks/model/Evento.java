package com.example.EventYunks.model;

import com.example.EventYunks.model.enums.EstadoEvento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "eventos")
@Getter
@Setter
@NoArgsConstructor
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nombre;

    @Column(length = 2000)
    private String descripcion;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recinto_id", nullable = false)
    private Recinto recinto;

    private String categoria;

    @Column(length = 500)
    private String urlImagen;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEvento estado = EstadoEvento.BORRADOR;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizador_id", nullable = false)
    private Organizador organizador;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Boleto> boletos = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "evento_artista",
               joinColumns = @JoinColumn(name = "evento_id"),
               inverseJoinColumns = @JoinColumn(name = "artista_id"))
    private List<Artista> artistas = new ArrayList<>();

    @OneToMany(mappedBy = "evento")
    private List<CambioEvento> cambios = new ArrayList<>();

    /**
     * Precio más bajo entre los boletos del evento, se usa para mostrar "Desde $X" en el catálogo
     * @return el precio mínimo, o null si el evento no tiene boletos con precio
     */
    public BigDecimal getPrecioDesde() {
        return boletos.stream()
                .map(Boleto::getPrecio)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    public void agregarBoleto(Boleto boleto) {
        boletos.add(boleto);
        boleto.setEvento(this);
    }
}
