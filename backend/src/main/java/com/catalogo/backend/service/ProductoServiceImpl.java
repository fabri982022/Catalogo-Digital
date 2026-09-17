package com.catalogo.backend.service;

import com.catalogo.backend.dto.ProductoDto;
import com.catalogo.backend.entity.Catalogo;
import com.catalogo.backend.entity.Categoria;
import com.catalogo.backend.entity.Producto;
import com.catalogo.backend.mapper.ProductoMapper;
import com.catalogo.backend.repository.CatalogoRepository;
import com.catalogo.backend.repository.CategoriaRepository;
import com.catalogo.backend.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CatalogoRepository catalogoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;

    public ProductoServiceImpl(ProductoRepository productoRepository,
            CatalogoRepository catalogoRepository,
            CategoriaRepository categoriaRepository,
            ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.catalogoRepository = catalogoRepository;
        this.categoriaRepository = categoriaRepository;
        this.productoMapper = productoMapper;
    }

    @Override
    public List<ProductoDto> findAll() {
        List<ProductoDto> productos = productoRepository.findAll().stream()
                .map(productoMapper::toDto)
                .toList();
        log.info("Productos encontrados: {}", productos.size());
        return productos;
    }

    @Override
    public ProductoDto findById(Long id) {
        log.info("Buscando producto id={}", id);
        return productoRepository.findById(id)
                .map(productoMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + id));
    }

    @Override
    public ProductoDto save(ProductoDto dto) {
        log.info("Guardando producto codigo={} catalogoId={} categoriaId={}", dto.getCodigo(), dto.getCatalogoId(),
                dto.getCategoriaId());
        Producto entity = productoMapper.toEntity(dto);

        if (dto.getCatalogoId() != null) {
            Catalogo catalogo = catalogoRepository.findById(dto.getCatalogoId())
                    .orElseThrow(() -> new EntityNotFoundException("Catalogo no encontrado: " + dto.getCatalogoId()));
            entity.setCatalogo(catalogo);
        }

        if (dto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria no encontrada: " + dto.getCategoriaId()));
            entity.setCategoria(categoria);
        }

        ProductoDto saved = productoMapper.toDto(productoRepository.save(entity));
        log.info("Producto guardado id={} codigo={}", saved.getId(), saved.getCodigo());
        return saved;
    }

    @Override
    public ProductoDto update(Long id, ProductoDto dto) {
        log.info("Actualizando producto id={} codigo={}", id, dto.getCodigo());
        Producto entity = productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + id));

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

        if (dto.getCatalogoId() != null) {
            Catalogo catalogo = catalogoRepository.findById(dto.getCatalogoId())
                    .orElseThrow(() -> new EntityNotFoundException("Catalogo no encontrado: " + dto.getCatalogoId()));
            entity.setCatalogo(catalogo);
        }

        if (dto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria no encontrada: " + dto.getCategoriaId()));
            entity.setCategoria(categoria);
        }

        return productoMapper.toDto(productoRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        log.info("Eliminando producto id={}", id);
        if (!productoRepository.existsById(id)) {
            throw new EntityNotFoundException("Producto no encontrado: " + id);
        }
        productoRepository.deleteById(id);
    }
}
