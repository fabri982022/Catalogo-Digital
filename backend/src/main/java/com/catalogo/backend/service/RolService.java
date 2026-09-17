package com.catalogo.backend.service;

import com.catalogo.backend.dto.RolDto;

import java.util.List;

public interface RolService {
    List<RolDto> findAll();

    RolDto findById(Long id);

    RolDto save(RolDto dto);

    RolDto update(Long id, RolDto dto);

    void deleteById(Long id);
}
