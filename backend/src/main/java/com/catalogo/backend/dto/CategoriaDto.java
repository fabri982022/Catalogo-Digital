package com.catalogo.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CategoriaDto {
    private Long id;
    @NotBlank
    private String nombre;
    private String descripcion;
    private String imagen;
    private String icono;
    private Integer orden;
    private Boolean activa;
    @NotNull
    @Positive
    private Long catalogoId;
    private Long categoriaPadreId;
    private List<Long> subcategoriaIds = new ArrayList<>();
    private List<Long> productoIds = new ArrayList<>();
}
