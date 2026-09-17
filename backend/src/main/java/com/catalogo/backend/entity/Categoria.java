package com.catalogo.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;
    private String descripcion;
    private String imagen;
    private String icono;
    private Integer orden;
    private Boolean activa = true;

    @ManyToOne(optional = false)
    private Catalogo catalogo;

    @ManyToOne
    private Categoria categoriaPadre;

    @OneToMany(mappedBy = "categoriaPadre")
    private List<Categoria> subcategorias = new ArrayList<>();

    @OneToMany(mappedBy = "categoria")
    private List<Producto> productos = new ArrayList<>();
}
