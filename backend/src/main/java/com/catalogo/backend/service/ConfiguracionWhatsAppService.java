package com.catalogo.backend.service;

import com.catalogo.backend.dto.ConfiguracionWhatsAppDto;

import java.util.List;

public interface ConfiguracionWhatsAppService {
    List<ConfiguracionWhatsAppDto> findAll();

    ConfiguracionWhatsAppDto findById(Long id);

    ConfiguracionWhatsAppDto save(ConfiguracionWhatsAppDto dto);

    ConfiguracionWhatsAppDto update(Long id, ConfiguracionWhatsAppDto dto);

    void deleteById(Long id);
}
