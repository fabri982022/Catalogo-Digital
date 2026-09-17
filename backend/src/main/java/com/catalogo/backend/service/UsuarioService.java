package com.catalogo.backend.service;

import com.catalogo.backend.dto.UsuarioDto;

import java.util.List;

public interface UsuarioService {
    List<UsuarioDto> findAll();

    UsuarioDto findById(Long id);

    UsuarioDto save(UsuarioDto dto);

    UsuarioDto update(Long id, UsuarioDto dto);

    void deleteById(Long id);
}
