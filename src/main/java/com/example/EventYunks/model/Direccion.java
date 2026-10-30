package com.example.EventYunks.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "La calle es obligatoria")
    @Size(max = 255, message = "La calle no puede pasar de 255 caracteres")
    private String calle;

    @NotBlank(message = "La colonia es obligatoria")
    @Size(max = 255, message = "La colonia no puede pasar de 255 caracteres")
    private String colonia;

    @NotBlank(message = "El número es obligatorio")
    @Size(max = 255, message = "El número no puede pasar de 255 caracteres")
    private String numero;

    @NotBlank(message = "El código postal es obligatorio y debe tener 5 dígitos")
    @Pattern(regexp = "\\d{5}", message = "El código postal es obligatorio y debe tener 5 dígitos")
    @Column(length = 10)
    private String codigoPostal;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 255, message = "La ciudad no puede pasar de 255 caracteres")
    private String ciudad;

    @Size(max = 255, message = "El nombre del lugar no puede pasar de 255 caracteres")
    private String nombreLugar;

    /**
     * Arma la dirección en una sola línea para mostrarla en las vistas (calle y número, colonia, ciudad)
     * @return la dirección en texto, o null si no tiene ningún dato
     */
    public String getResumen() {
        String calleNumero = limpiar(calle);
        if (calleNumero != null && limpiar(numero) != null) {
            calleNumero += " " + numero.trim();
        }
        java.util.List<String> partes = new java.util.ArrayList<>();
        for (String parte : new String[]{calleNumero, limpiar(colonia), limpiar(ciudad)}) {
            if (parte != null) {
                partes.add(parte);
            }
        }
        return partes.isEmpty() ? null : String.join(", ", partes);
    }

    private String limpiar(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }
}
