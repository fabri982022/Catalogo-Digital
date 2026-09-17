package com.catalogo.backend.service;

import com.catalogo.backend.dto.CategoriaDto;

import java.util.List;

public interface CategoriaService {
    List<CategoriaDto> findAll();

    CategoriaDto findById(Long id);

    CategoriaDto save(CategoriaDto dto);

    CategoriaDto update(Long id, CategoriaDto dto);

    void deleteById(Long id);
}
