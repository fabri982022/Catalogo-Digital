package com.catalogo.backend.mapper;

import com.catalogo.backend.dto.PuertaDto;
import com.catalogo.backend.entity.Puerta;
import org.springframework.stereotype.Component;

@Component
public class PuertaMapper {
    private final ProductoMapper productoMapper;

    public PuertaMapper(ProductoMapper productoMapper) {
        this.productoMapper = productoMapper;
    }

    public PuertaDto toDto(Puerta entity) {
        if (entity == null)
            return null;
        PuertaDto dto = new PuertaDto();
        productoMapper.copyToDto(entity, dto);
        dto.setManija(entity.getManija());
        dto.setTipo(entity.getTipo());
        dto.setSeguridad(entity.getSeguridad());
        dto.setSentidoApertura(entity.getSentidoApertura());
        dto.setMaterial(entity.getMaterial());
        return dto;
    }

    public Puerta toEntity(PuertaDto dto) {
        if (dto == null)
            return null;
        Puerta entity = new Puerta();
        productoMapper.copyToEntity(dto, entity);
        entity.setManija(dto.getManija());
        entity.setTipo(dto.getTipo());
        entity.setSeguridad(dto.getSeguridad());
        entity.setSentidoApertura(dto.getSentidoApertura());
        entity.setMaterial(dto.getMaterial());
        return entity;
    }
}
