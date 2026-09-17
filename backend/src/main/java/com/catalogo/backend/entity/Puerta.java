package com.catalogo.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "puertas")
@Getter
@Setter
@NoArgsConstructor
public class Puerta extends Producto {

    private String manija;
    private String tipo;
    private Boolean seguridad;
    private String sentidoApertura;
    private String material;
}
