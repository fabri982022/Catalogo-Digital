package com.catalogo.backend.service;

import com.catalogo.backend.dto.CatalogoDto;

import java.util.List;

public interface CatalogoService {
    List<CatalogoDto> findAll();

    CatalogoDto findById(Long id);

    CatalogoDto save(CatalogoDto dto);

    CatalogoDto update(Long id, CatalogoDto dto);

    void deleteById(Long id);
}
