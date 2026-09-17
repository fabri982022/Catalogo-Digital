package com.catalogo.backend.service;

import com.catalogo.backend.dto.StockDto;
import com.catalogo.backend.entity.Producto;
import com.catalogo.backend.entity.Stock;
import com.catalogo.backend.mapper.StockMapper;
import com.catalogo.backend.repository.ProductoRepository;
import com.catalogo.backend.repository.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final ProductoRepository productoRepository;
    private final StockMapper stockMapper;

    public StockServiceImpl(StockRepository stockRepository,
            ProductoRepository productoRepository,
            StockMapper stockMapper) {
        this.stockRepository = stockRepository;
        this.productoRepository = productoRepository;
        this.stockMapper = stockMapper;
    }

    @Override
    public List<StockDto> findAll() {
        return stockRepository.findAll().stream()
                .map(stockMapper::toDto)
                .toList();
    }

    @Override
    public StockDto findById(Long id) {
        return stockRepository.findById(id)
                .map(stockMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Stock no encontrado: " + id));
    }

    @Override
    public StockDto save(StockDto dto) {
        Stock entity = stockMapper.toEntity(dto);

        if (dto.getProductoId() != null) {
            Producto producto = productoRepository.findById(dto.getProductoId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + dto.getProductoId()));
            entity.setProducto(producto);
        }

        return stockMapper.toDto(stockRepository.save(entity));
    }

    @Override
    public StockDto update(Long id, StockDto dto) {
        Stock entity = stockRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stock no encontrado: " + id));

        entity.setCantidadDisponible(dto.getCantidadDisponible());
        entity.setCantidadMinima(dto.getCantidadMinima());
        entity.setPermiteBajoPedido(dto.getPermiteBajoPedido());

        if (dto.getProductoId() != null) {
            Producto producto = productoRepository.findById(dto.getProductoId())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + dto.getProductoId()));
            entity.setProducto(producto);
        }

        return stockMapper.toDto(stockRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        if (!stockRepository.existsById(id)) {
            throw new EntityNotFoundException("Stock no encontrado: " + id);
        }
        stockRepository.deleteById(id);
    }
}
