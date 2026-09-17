package com.catalogo.backend.service;

import com.catalogo.backend.dto.PaginaDto;

import java.util.List;

public interface PaginaService {
    List<PaginaDto> findAll();

    PaginaDto findById(Long id);

    PaginaDto save(PaginaDto dto);

    PaginaDto update(Long id, PaginaDto dto);

    void deleteById(Long id);
}
