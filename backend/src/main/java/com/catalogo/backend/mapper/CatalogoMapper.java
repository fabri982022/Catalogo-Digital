package com.catalogo.backend.mapper;

import com.catalogo.backend.dto.CatalogoDto;
import com.catalogo.backend.entity.Catalogo;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class CatalogoMapper {
    public CatalogoDto toDto(Catalogo entity) {
        if (entity == null)
            return null;
        CatalogoDto dto = new CatalogoDto();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setNumeroWhatsapp(entity.getNumeroWhatsapp());
        dto.setFechaCreacion(entity.getFechaCreacion());
        dto.setFechaActualizacion(entity.getFechaActualizacion());
        dto.setUsuarioId(entity.getUsuario() == null ? null : entity.getUsuario().getId());
        dto.setCategoriaIds(entity.getCategorias() == null ? Collections.emptyList()
                : entity.getCategorias().stream().map(categoria -> categoria.getId()).toList());
        dto.setProductoIds(entity.getProductos() == null ? Collections.emptyList()
                : entity.getProductos().stream().map(producto -> producto.getId()).toList());
        dto.setPaginaIds(entity.getPaginas() == null ? Collections.emptyList()
                : entity.getPaginas().stream().map(pagina -> pagina.getId()).toList());
        dto.setConfiguracionWhatsAppId(
                entity.getConfiguracionWhatsApp() == null ? null : entity.getConfiguracionWhatsApp().getId());
        return dto;
    }

    public Catalogo toEntity(CatalogoDto dto) {
        if (dto == null)
            return null;
        Catalogo entity = new Catalogo();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setNumeroWhatsapp(dto.getNumeroWhatsapp());
        return entity;
    }
}
