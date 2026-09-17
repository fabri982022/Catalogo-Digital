package com.catalogo.backend.repository;

import com.catalogo.backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @EntityGraph(attributePaths = {"roles", "roles.rol"})
    java.util.Optional<Usuario> findByNombreUsuarioAndActivoTrue(String nombreUsuario);
}
