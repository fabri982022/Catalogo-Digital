package com.catalogo.backend.mapper;

import com.catalogo.backend.dto.VentanaDto;
import com.catalogo.backend.entity.Ventana;
import org.springframework.stereotype.Component;

@Component
public class VentanaMapper {
    private final ProductoMapper productoMapper;

    public VentanaMapper(ProductoMapper productoMapper) {
        this.productoMapper = productoMapper;
    }

    public VentanaDto toDto(Ventana entity) {
        if (entity == null)
            return null;
        VentanaDto dto = new VentanaDto();
        productoMapper.copyToDto(entity, dto);
        dto.setLinea(entity.getLinea());
        dto.setTipo(entity.getTipo());
        dto.setSeguridad(entity.getSeguridad());
        dto.setMaterial(entity.getMaterial());
        dto.setSistemaApertura(entity.getSistemaApertura());
        return dto;
    }

    public Ventana toEntity(VentanaDto dto) {
        if (dto == null)
            return null;
        Ventana entity = new Ventana();
        productoMapper.copyToEntity(dto, entity);
        entity.setLinea(dto.getLinea());
        entity.setTipo(dto.getTipo());
        entity.setSeguridad(dto.getSeguridad());
        entity.setMaterial(dto.getMaterial());
        entity.setSistemaApertura(dto.getSistemaApertura());
        return entity;
    }
}
