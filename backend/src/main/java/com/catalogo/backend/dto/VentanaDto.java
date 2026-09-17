package com.catalogo.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VentanaDto extends ProductoDto {
    private String linea;
    private String tipo;
    private Boolean seguridad;
    private String material;
    private String sistemaApertura;
}
