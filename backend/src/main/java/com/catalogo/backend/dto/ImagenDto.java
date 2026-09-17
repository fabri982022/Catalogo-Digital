package com.catalogo.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImagenDto {
    private Long id;
    private String url;
    private String publicId;
    private String nombre;
    private Boolean esPrincipal;
    private Integer orden;
    @NotNull
    private Long productoId;
}
