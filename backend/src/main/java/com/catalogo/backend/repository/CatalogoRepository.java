package com.catalogo.backend.repository;

import com.catalogo.backend.entity.Catalogo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogoRepository extends JpaRepository<Catalogo, Long> {
}
