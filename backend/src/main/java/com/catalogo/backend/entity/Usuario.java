package com.catalogo.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombreUsuario;

    @NotBlank
    private String contrasena;

    @Email
    private String email;

    private Boolean activo = true;
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    private LocalDateTime ultimoAcceso;

    @OneToOne
    private Catalogo catalogo;

    @OneToMany(mappedBy = "usuario")
    private List<UsuarioRol> roles = new ArrayList<>();
}
