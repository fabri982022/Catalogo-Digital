package com.catalogo.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ventanas")
@Getter
@Setter
@NoArgsConstructor
public class Ventana extends Producto {

    private String linea;
    private String tipo;
    private Boolean seguridad;
    private String material;
    private String sistemaApertura;
}
