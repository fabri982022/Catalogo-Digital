package com.catalogo.backend.mapper;

import com.catalogo.backend.dto.ImagenDto;
import com.catalogo.backend.entity.Imagen;
import org.springframework.stereotype.Component;

@Component
public class ImagenMapper {
    public ImagenDto toDto(Imagen entity) {
        if (entity == null)
            return null;
        ImagenDto dto = new ImagenDto();
        dto.setId(entity.getId());
        dto.setUrl(entity.getUrl());
        dto.setPublicId(entity.getPublicId());
        dto.setNombre(entity.getNombre());
        dto.setEsPrincipal(entity.getEsPrincipal());
        dto.setOrden(entity.getOrden());
        dto.setProductoId(entity.getProducto() == null ? null : entity.getProducto().getId());
        return dto;
    }

    public Imagen toEntity(ImagenDto dto) {
        if (dto == null)
            return null;
        Imagen entity = new Imagen();
        entity.setUrl(dto.getUrl());
        entity.setPublicId(dto.getPublicId());
        entity.setNombre(dto.getNombre());
        entity.setEsPrincipal(dto.getEsPrincipal());
        entity.setOrden(dto.getOrden());
        return entity;
    }
}
