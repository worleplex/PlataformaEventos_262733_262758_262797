package com.example.EventYunks.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class BoletoBorradorDTO implements Serializable {
    private String tipo;
    private BigDecimal precio;
    private Integer cantidad;
    private String descripcion;

    private Long boletoId;
}
