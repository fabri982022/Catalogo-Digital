package com.catalogo.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RolDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private List<Long> usuarioRolIds = new ArrayList<>();
}
