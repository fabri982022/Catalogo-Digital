package com.catalogo.backend.service;

import com.catalogo.backend.dto.ProductoDto;

import java.util.List;

public interface ProductoService {
    List<ProductoDto> findAll();

    ProductoDto findById(Long id);

    ProductoDto save(ProductoDto dto);

    ProductoDto update(Long id, ProductoDto dto);

    void deleteById(Long id);
}
