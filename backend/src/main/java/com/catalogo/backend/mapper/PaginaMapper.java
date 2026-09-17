package com.catalogo.backend.mapper;

import com.catalogo.backend.dto.PaginaDto;
import com.catalogo.backend.entity.Pagina;
import org.springframework.stereotype.Component;

@Component
public class PaginaMapper {
    public PaginaDto toDto(Pagina entity) {
        if (entity == null)
            return null;
        PaginaDto dto = new PaginaDto();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setSlug(entity.getSlug());
        dto.setContenido(entity.getContenido());
        dto.setImagen(entity.getImagen());
        dto.setOrden(entity.getOrden());
        dto.setActiva(entity.getActiva());
        dto.setCatalogoId(entity.getCatalogo() == null ? null : entity.getCatalogo().getId());
        return dto;
    }

    public Pagina toEntity(PaginaDto dto) {
        if (dto == null)
            return null;
        Pagina entity = new Pagina();
        entity.setTitulo(dto.getTitulo());
        entity.setSlug(dto.getSlug());
        entity.setContenido(dto.getContenido());
        entity.setImagen(dto.getImagen());
        entity.setOrden(dto.getOrden());
        entity.setActiva(dto.getActiva());
        return entity;
    }
}
