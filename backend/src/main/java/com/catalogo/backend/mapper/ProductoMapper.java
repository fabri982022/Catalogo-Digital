package com.catalogo.backend.mapper;

import com.catalogo.backend.dto.ProductoDto;
import com.catalogo.backend.entity.Imagen;
import com.catalogo.backend.entity.Producto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class ProductoMapper {

    public ProductoDto toDto(Producto entity) {
        if (entity == null)
            return null;
        ProductoDto dto = new ProductoDto();
        copyToDto(entity, dto);
        return dto;
    }

    public Producto toEntity(ProductoDto dto) {
        if (dto == null)
            return null;
        Producto entity = new Producto();
        copyToEntity(dto, entity);
        return entity;
    }

    public void copyToDto(Producto entity, ProductoDto dto) {
        dto.setId(entity.getId());
        dto.setCodigo(entity.getCodigo());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setColor(entity.getColor());
        dto.setTerminacion(entity.getTerminacion());
        dto.setAncho(entity.getAncho());
        dto.setAltura(entity.getAltura());
        dto.setPrecio(entity.getPrecio());
        dto.setEstado(entity.getEstado());
        dto.setDestacado(entity.isDestacado());
        dto.setFechaCreacion(entity.getFechaCreacion());
        dto.setFechaActualizacion(entity.getFechaActualizacion());
        dto.setCatalogoId(entity.getCatalogo() == null ? null : entity.getCatalogo().getId());
        dto.setCategoriaId(entity.getCategoria() == null ? null : entity.getCategoria().getId());
        dto.setTipoProducto(entity.getTipoProducto());
        dto.setImagenIds(entity.getImagenes() == null ? Collections.emptyList()
                : entity.getImagenes().stream().map(Imagen::getId).toList());
        dto.setImagenUrl(entity.getImagenes() == null || entity.getImagenes().isEmpty() ? null
                : entity.getImagenes().stream()
                        .filter(imagen -> Boolean.TRUE.equals(imagen.getEsPrincipal()))
                        .findFirst()
                        .orElse(entity.getImagenes().getFirst())
                        .getUrl());
        dto.setStockId(entity.getStock() == null ? null : entity.getStock().getId());
        dto.setStock(entity.getStock() == null ? 0 : entity.getStock().getCantidadDisponible());
    }

    public void copyToEntity(ProductoDto dto, Producto entity) {
        entity.setCodigo(dto.getCodigo());
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setColor(dto.getColor());
        entity.setTerminacion(dto.getTerminacion());
        entity.setAncho(dto.getAncho());
        entity.setAltura(dto.getAltura());
        entity.setPrecio(dto.getPrecio());
        entity.setEstado(dto.getEstado());
        entity.setDestacado(dto.isDestacado());
        entity.setTipoProducto(dto.getTipoProducto());
    }
}
