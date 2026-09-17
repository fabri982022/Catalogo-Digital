package com.catalogo.backend.mapper;

import com.catalogo.backend.dto.CategoriaDto;
import com.catalogo.backend.entity.Categoria;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class CategoriaMapper {
    public CategoriaDto toDto(Categoria entity) {
        if (entity == null)
            return null;
        CategoriaDto dto = new CategoriaDto();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setImagen(entity.getImagen());
        dto.setIcono(entity.getIcono());
        dto.setOrden(entity.getOrden());
        dto.setActiva(entity.getActiva());
        dto.setCatalogoId(entity.getCatalogo() == null ? null : entity.getCatalogo().getId());
        dto.setCategoriaPadreId(entity.getCategoriaPadre() == null ? null : entity.getCategoriaPadre().getId());
        dto.setSubcategoriaIds(entity.getSubcategorias() == null ? Collections.emptyList()
                : entity.getSubcategorias().stream().map(categoria -> categoria.getId()).toList());
        dto.setProductoIds(entity.getProductos() == null ? Collections.emptyList()
                : entity.getProductos().stream().map(producto -> producto.getId()).toList());
        return dto;
    }

    public Categoria toEntity(CategoriaDto dto) {
        if (dto == null)
            return null;
        Categoria entity = new Categoria();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setImagen(dto.getImagen());
        entity.setIcono(dto.getIcono());
        entity.setOrden(dto.getOrden());
        entity.setActiva(dto.getActiva());
        return entity;
    }
}
