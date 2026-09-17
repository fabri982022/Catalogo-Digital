package com.catalogo.backend.service;

import com.catalogo.backend.dto.RolDto;
import com.catalogo.backend.entity.Rol;
import com.catalogo.backend.mapper.RolMapper;
import com.catalogo.backend.repository.RolRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;
    private final RolMapper rolMapper;

    public RolServiceImpl(RolRepository rolRepository, RolMapper rolMapper) {
        this.rolRepository = rolRepository;
        this.rolMapper = rolMapper;
    }

    @Override
    public List<RolDto> findAll() {
        return rolRepository.findAll().stream()
                .map(rolMapper::toDto)
                .toList();
    }

    @Override
    public RolDto findById(Long id) {
        return rolRepository.findById(id)
                .map(rolMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado: " + id));
    }

    @Override
    public RolDto save(RolDto dto) {
        Rol entity = rolMapper.toEntity(dto);
        return rolMapper.toDto(rolRepository.save(entity));
    }

    @Override
    public RolDto update(Long id, RolDto dto) {
        Rol entity = rolRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado: " + id));

        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());

        return rolMapper.toDto(rolRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        if (!rolRepository.existsById(id)) {
            throw new EntityNotFoundException("Rol no encontrado: " + id);
        }
        rolRepository.deleteById(id);
    }
}
