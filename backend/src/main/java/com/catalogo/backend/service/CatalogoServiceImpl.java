package com.catalogo.backend.service;

import com.catalogo.backend.dto.CatalogoDto;
import com.catalogo.backend.entity.Catalogo;
import com.catalogo.backend.mapper.CatalogoMapper;
import com.catalogo.backend.repository.CatalogoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class CatalogoServiceImpl implements CatalogoService {

    private final CatalogoRepository catalogoRepository;
    private final CatalogoMapper catalogoMapper;

    public CatalogoServiceImpl(CatalogoRepository catalogoRepository, CatalogoMapper catalogoMapper) {
        this.catalogoRepository = catalogoRepository;
        this.catalogoMapper = catalogoMapper;
    }

    @Override
    public List<CatalogoDto> findAll() {
        return catalogoRepository.findAll().stream()
                .map(catalogoMapper::toDto)
                .toList();
    }

    @Override
    public CatalogoDto findById(Long id) {
        return catalogoRepository.findById(id)
                .map(catalogoMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Catalogo no encontrado: " + id));
    }

    @Override
    public CatalogoDto save(CatalogoDto dto) {
        Catalogo entity = catalogoMapper.toEntity(dto);
        return catalogoMapper.toDto(catalogoRepository.save(entity));
    }

    @Override
    public CatalogoDto update(Long id, CatalogoDto dto) {
        Catalogo entity = catalogoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Catalogo no encontrado: " + id));

        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setNumeroWhatsapp(dto.getNumeroWhatsapp());

        return catalogoMapper.toDto(catalogoRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        if (!catalogoRepository.existsById(id)) {
            throw new EntityNotFoundException("Catalogo no encontrado: " + id);
        }
        catalogoRepository.deleteById(id);
    }
}
