package com.catalogo.backend.repository;

import com.catalogo.backend.entity.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagenRepository extends JpaRepository<Imagen, Long> {
}
