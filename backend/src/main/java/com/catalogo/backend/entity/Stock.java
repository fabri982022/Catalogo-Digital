package com.catalogo.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cantidadDisponible = 0;
    private Integer cantidadMinima;
    private Boolean permiteBajoPedido = false;
    private LocalDateTime ultimaActualizacion = LocalDateTime.now();

    @OneToOne(optional = false)
    private Producto producto;
}
