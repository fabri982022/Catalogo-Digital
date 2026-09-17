package com.catalogo.backend.service;

import com.catalogo.backend.dto.CategoriaDto;
import com.catalogo.backend.entity.Catalogo;
import com.catalogo.backend.entity.Categoria;
import com.catalogo.backend.mapper.CategoriaMapper;
import com.catalogo.backend.repository.CatalogoRepository;
import com.catalogo.backend.repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CatalogoRepository catalogoRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository,
            CatalogoRepository catalogoRepository,
            CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.catalogoRepository = catalogoRepository;
        this.categoriaMapper = categoriaMapper;
    }

    @Override
    public List<CategoriaDto> findAll() {
        return categoriaRepository.findAllWithRelations().stream()
                .map(categoriaMapper::toDto)
                .toList();
    }

    @Override
    public CategoriaDto findById(Long id) {
        return categoriaRepository.findByIdWithSubcategorias(id)
                .map(categoriaMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Categoria no encontrada: " + id));
    }

    @Override
    public CategoriaDto save(CategoriaDto dto) {
        Categoria entity = categoriaMapper.toEntity(dto);

        if (dto.getCatalogoId() != null) {
            Catalogo catalogo = catalogoRepository.findById(dto.getCatalogoId())
                    .orElseThrow(() -> new EntityNotFoundException("Catalogo no encontrado: " + dto.getCatalogoId()));
            entity.setCatalogo(catalogo);
        }

        if (dto.getCategoriaPadreId() != null) {
            Categoria padre = categoriaRepository.findById(dto.getCategoriaPadreId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Categoria padre no encontrada: " + dto.getCategoriaPadreId()));
            entity.setCategoriaPadre(padre);
        }

        Categoria savedEntity = categoriaRepository.save(entity);
        return categoriaMapper.toDto(categoriaRepository.findByIdWithSubcategorias(savedEntity.getId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria no encontrada: " + savedEntity.getId())));
    }

    @Override
    public CategoriaDto update(Long id, CategoriaDto dto) {
        Categoria entity = categoriaRepository.findByIdWithSubcategorias(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria no encontrada: " + id));

        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setImagen(dto.getImagen());
        entity.setIcono(dto.getIcono());
        entity.setOrden(dto.getOrden());
        entity.setActiva(dto.getActiva());

        if (dto.getCatalogoId() != null) {
            Catalogo catalogo = catalogoRepository.findById(dto.getCatalogoId())
                    .orElseThrow(() -> new EntityNotFoundException("Catalogo no encontrado: " + dto.getCatalogoId()));
            entity.setCatalogo(catalogo);
        }

        if (dto.getCategoriaPadreId() != null) {
            Categoria padre = categoriaRepository.findById(dto.getCategoriaPadreId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Categoria padre no encontrada: " + dto.getCategoriaPadreId()));
            entity.setCategoriaPadre(padre);
        }

        categoriaRepository.save(entity);
        return categoriaMapper.toDto(categoriaRepository.findByIdWithSubcategorias(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria no encontrada: " + id)));
    }

    @Override
    public void deleteById(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new EntityNotFoundException("Categoria no encontrada: " + id);
        }
        categoriaRepository.deleteById(id);
    }
}
