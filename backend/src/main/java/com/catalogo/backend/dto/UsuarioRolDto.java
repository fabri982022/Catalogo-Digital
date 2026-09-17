package com.catalogo.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioRolDto {
    private Long id;
    @NotNull
    private Long usuarioId;
    @NotNull
    private Long rolId;
}
