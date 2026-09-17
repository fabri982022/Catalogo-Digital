package com.catalogo.backend.service;

import com.catalogo.backend.dto.StockDto;

import java.util.List;

public interface StockService {
    List<StockDto> findAll();

    StockDto findById(Long id);

    StockDto save(StockDto dto);

    StockDto update(Long id, StockDto dto);

    void deleteById(Long id);
}
