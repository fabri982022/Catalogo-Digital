package com.catalogo.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaginaDto {
    private Long id;
    @NotBlank
    private String titulo;
    private String slug;
    private String contenido;
    private String imagen;
    private Integer orden;
    private Boolean activa;
    @NotNull
    private Long catalogoId;
}
