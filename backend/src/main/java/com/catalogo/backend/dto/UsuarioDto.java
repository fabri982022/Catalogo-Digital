package com.catalogo.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UsuarioDto {
    private Long id;
    @NotBlank
    private String nombreUsuario;
    @NotBlank
    private String contrasena;
    @Email
    private String email;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimoAcceso;
    private Long catalogoId;
    private List<Long> rolIds = new ArrayList<>();
}
