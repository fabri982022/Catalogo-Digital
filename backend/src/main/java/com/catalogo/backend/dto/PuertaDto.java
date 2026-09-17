package com.catalogo.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PuertaDto extends ProductoDto {
    private String manija;
    private String tipo;
    private Boolean seguridad;
    private String sentidoApertura;
    private String material;
}
