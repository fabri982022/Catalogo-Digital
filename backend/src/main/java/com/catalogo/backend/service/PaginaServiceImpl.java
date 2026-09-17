package com.catalogo.backend.service;

import com.catalogo.backend.dto.PaginaDto;
import com.catalogo.backend.entity.Catalogo;
import com.catalogo.backend.entity.Pagina;
import com.catalogo.backend.mapper.PaginaMapper;
import com.catalogo.backend.repository.CatalogoRepository;
import com.catalogo.backend.repository.PaginaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class PaginaServiceImpl implements PaginaService {

    private final PaginaRepository paginaRepository;
    private final CatalogoRepository catalogoRepository;
    private final PaginaMapper paginaMapper;

    public PaginaServiceImpl(PaginaRepository paginaRepository,
            CatalogoRepository catalogoRepository,
            PaginaMapper paginaMapper) {
        this.paginaRepository = paginaRepository;
        this.catalogoRepository = catalogoRepository;
        this.paginaMapper = paginaMapper;
    }

    @Override
    public List<PaginaDto> findAll() {
        return paginaRepository.findAll().stream()
                .map(paginaMapper::toDto)
                .toList();
    }

    @Override
    public PaginaDto findById(Long id) {
        return paginaRepository.findById(id)
                .map(paginaMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Pagina no encontrada: " + id));
    }

    @Override
    public PaginaDto save(PaginaDto dto) {
        Pagina entity = paginaMapper.toEntity(dto);

        if (dto.getCatalogoId() != null) {
            Catalogo catalogo = catalogoRepository.findById(dto.getCatalogoId())
                    .orElseThrow(() -> new EntityNotFoundException("Catalogo no encontrado: " + dto.getCatalogoId()));
            entity.setCatalogo(catalogo);
        }

        return paginaMapper.toDto(paginaRepository.save(entity));
    }

    @Override
    public PaginaDto update(Long id, PaginaDto dto) {
        Pagina entity = paginaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pagina no encontrada: " + id));

        entity.setTitulo(dto.getTitulo());
        entity.setSlug(dto.getSlug());
        entity.setContenido(dto.getContenido());
        entity.setImagen(dto.getImagen());
        entity.setOrden(dto.getOrden());
        entity.setActiva(dto.getActiva());

        if (dto.getCatalogoId() != null) {
            Catalogo catalogo = catalogoRepository.findById(dto.getCatalogoId())
                    .orElseThrow(() -> new EntityNotFoundException("Catalogo no encontrado: " + dto.getCatalogoId()));
            entity.setCatalogo(catalogo);
        }

        return paginaMapper.toDto(paginaRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        if (!paginaRepository.existsById(id)) {
            throw new EntityNotFoundException("Pagina no encontrada: " + id);
        }
        paginaRepository.deleteById(id);
    }
}
