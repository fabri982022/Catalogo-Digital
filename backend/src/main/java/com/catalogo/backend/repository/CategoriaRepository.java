package com.catalogo.backend.repository;

import com.catalogo.backend.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    @Query("SELECT DISTINCT c FROM Categoria c LEFT JOIN FETCH c.subcategorias WHERE c.id = :id")
    Optional<Categoria> findByIdWithSubcategorias(@Param("id") Long id);

    @Query("SELECT c FROM Categoria c")
    List<Categoria> findAllWithRelations();
}