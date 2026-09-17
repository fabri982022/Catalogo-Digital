package com.catalogo.backend.mapper;

import com.catalogo.backend.dto.ConfiguracionWhatsAppDto;
import com.catalogo.backend.entity.ConfiguracionWhatsApp;
import org.springframework.stereotype.Component;

@Component
public class ConfiguracionWhatsAppMapper {
    public ConfiguracionWhatsAppDto toDto(ConfiguracionWhatsApp entity) {
        if (entity == null)
            return null;
        ConfiguracionWhatsAppDto dto = new ConfiguracionWhatsAppDto();
        dto.setId(entity.getId());
        dto.setNumero(entity.getNumero());
        dto.setMensajePersonalizado(entity.getMensajePersonalizado());
        dto.setActiva(entity.getActiva());
        dto.setCatalogoId(entity.getCatalogo() == null ? null : entity.getCatalogo().getId());
        return dto;
    }

    public ConfiguracionWhatsApp toEntity(ConfiguracionWhatsAppDto dto) {
        if (dto == null)
            return null;
        ConfiguracionWhatsApp entity = new ConfiguracionWhatsApp();
        entity.setNumero(dto.getNumero());
        entity.setMensajePersonalizado(dto.getMensajePersonalizado());
        entity.setActiva(dto.getActiva());
        return entity;
    }
}
