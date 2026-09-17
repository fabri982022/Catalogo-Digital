package com.catalogo.backend.dto;

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
public class CatalogoDto {
    private Long id;
    @NotBlank
    private String nombre;
    private String descripcion;
    private String numeroWhatsapp;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private Long usuarioId;
    private List<Long> categoriaIds = new ArrayList<>();
    private List<Long> productoIds = new ArrayList<>();
    private List<Long> paginaIds = new ArrayList<>();
    private Long configuracionWhatsAppId;
}
