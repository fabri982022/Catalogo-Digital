package com.catalogo.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class StockDto {
    private Long id;
    @PositiveOrZero
    private Integer cantidadDisponible;
    @PositiveOrZero
    private Integer cantidadMinima;
    private Boolean permiteBajoPedido;
    private LocalDateTime ultimaActualizacion;
    @NotNull
    @Positive
    private Long productoId;
}
