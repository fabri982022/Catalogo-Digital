package com.catalogo.backend.dto;

import com.catalogo.backend.enums.EstadoProducto;
import com.catalogo.backend.enums.TipoProducto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductoDto {
    private Long id;
    @NotBlank
    private String codigo;
    @NotBlank
    private String nombre;
    private String descripcion;
    private String color;
    private String terminacion;
    private Double ancho;
    private Double altura;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal precio;
    private EstadoProducto estado;
    private boolean destacado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    @NotNull
    @Positive
    private Long catalogoId;
    @Positive
    private Long categoriaId;
    private TipoProducto tipoProducto;
    private List<Long> imagenIds = new ArrayList<>();
    private String imagenUrl;
    private Long stockId;
    private Integer stock;
}
