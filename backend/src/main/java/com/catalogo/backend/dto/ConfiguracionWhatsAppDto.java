package com.catalogo.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConfiguracionWhatsAppDto {
    private Long id;
    private String numero;
    private String mensajePersonalizado;
    private Boolean activa;
    @NotNull
    private Long catalogoId;
}
