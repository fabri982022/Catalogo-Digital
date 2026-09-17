package com.catalogo.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.catalogo.backend.enums.EstadoProducto;
import com.catalogo.backend.enums.TipoProducto;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String codigo;

    @NotBlank
    private String nombre;

    private String descripcion;
    private String color;
    private String terminacion;
    private Double ancho;
    private Double altura;

    @ManyToOne(optional = false)
    private Catalogo catalogo;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal precio;

    @Enumerated(EnumType.STRING)
    private EstadoProducto estado = EstadoProducto.DISPONIBLE;
    private boolean destacado;
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    @ManyToOne
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    private TipoProducto tipoProducto;

    @OneToMany(mappedBy = "producto", orphanRemoval = true)
    private List<Imagen> imagenes = new ArrayList<>();

    @OneToOne(mappedBy = "producto", orphanRemoval = true)
    private Stock stock;
}
