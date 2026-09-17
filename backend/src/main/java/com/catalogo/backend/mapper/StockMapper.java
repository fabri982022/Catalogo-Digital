package com.catalogo.backend.mapper;

import com.catalogo.backend.dto.StockDto;
import com.catalogo.backend.entity.Stock;
import org.springframework.stereotype.Component;

@Component
public class StockMapper {
    public StockDto toDto(Stock entity) {
        if (entity == null)
            return null;
        StockDto dto = new StockDto();
        dto.setId(entity.getId());
        dto.setCantidadDisponible(entity.getCantidadDisponible());
        dto.setCantidadMinima(entity.getCantidadMinima());
        dto.setPermiteBajoPedido(entity.getPermiteBajoPedido());
        dto.setUltimaActualizacion(entity.getUltimaActualizacion());
        dto.setProductoId(entity.getProducto() == null ? null : entity.getProducto().getId());
        return dto;
    }

    public Stock toEntity(StockDto dto) {
        if (dto == null)
            return null;
        Stock entity = new Stock();
        entity.setCantidadDisponible(dto.getCantidadDisponible());
        entity.setCantidadMinima(dto.getCantidadMinima());
        entity.setPermiteBajoPedido(dto.getPermiteBajoPedido());
        return entity;
    }
}
