package com.example.EventYunks.Dominio;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "direcciones")
@Getter
@Setter
@NoArgsConstructor
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String calle;

    private String colonia;

    private String numero;

    @Column(length = 10)
    private String codigoPostal;

    @NotBlank
    private String ciudad;

    private String nombreLugar;
}
