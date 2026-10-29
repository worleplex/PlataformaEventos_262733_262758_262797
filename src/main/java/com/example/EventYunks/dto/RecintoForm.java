package com.example.EventYunks.dto;

import com.example.EventYunks.model.Direccion;
import com.example.EventYunks.model.Recinto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * objeto que representa los datos capturados en el formulario de crear/editar recinto que hicimos
 * solojunta en un solo objeto los datos del recinto y de su dirección, y contiene las validaciones
 * del formulario
 */
@Getter
@Setter
@NoArgsConstructor
public class RecintoForm {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede pasar de 255 caracteres")
    private String nombre;

    @NotNull(message = "La capacidad es obligatoria")
    @Positive(message = "La capacidad debe ser un número mayor a 0")
    private Integer capacidad;

    @Size(max = 255, message = "El nombre del lugar no puede pasar de 255 caracteres")
    private String nombreLugar;

    @NotBlank(message = "La calle es obligatoria")
    @Size(max = 255, message = "La calle no puede pasar de 255 caracteres")
    private String calle;

    @NotBlank(message = "El número es obligatorio")
    @Size(max = 255, message = "El número no puede pasar de 255 caracteres")
    private String numero;

    @NotBlank(message = "La colonia es obligatoria")
    @Size(max = 255, message = "La colonia no puede pasar de 255 caracteres")
    private String colonia;

    @NotBlank(message = "El código postal es obligatorio y debe tener 5 dígitos")
    @Pattern(regexp = "\\d{5}", message = "El código postal es obligatorio y debe tener 5 dígitos")
    private String codigoPostal;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 255, message = "La ciudad no puede pasar de 255 caracteres")
    private String ciudad;

    public static RecintoForm desde(Recinto recinto) {
        RecintoForm form = new RecintoForm();
        form.setId(recinto.getId());
        form.setNombre(recinto.getNombre());
        form.setCapacidad(recinto.getCapacidad());
        Direccion d = recinto.getDireccion();
        if (d != null) {
            form.setNombreLugar(d.getNombreLugar());
            form.setCalle(d.getCalle());
            form.setNumero(d.getNumero());
            form.setColonia(d.getColonia());
            form.setCodigoPostal(d.getCodigoPostal());
            form.setCiudad(d.getCiudad());
        }
        return form;
    }
}
