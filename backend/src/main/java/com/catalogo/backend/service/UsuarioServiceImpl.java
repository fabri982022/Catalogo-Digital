package com.catalogo.backend.service;

import com.catalogo.backend.dto.UsuarioDto;
import com.catalogo.backend.entity.Catalogo;
import com.catalogo.backend.entity.Usuario;
import com.catalogo.backend.mapper.UsuarioMapper;
import com.catalogo.backend.repository.CatalogoRepository;
import com.catalogo.backend.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final CatalogoRepository catalogoRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
            CatalogoRepository catalogoRepository,
            UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.catalogoRepository = catalogoRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public List<UsuarioDto> findAll() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toDto)
                .toList();
    }

    @Override
    public UsuarioDto findById(Long id) {
        return usuarioRepository.findById(id)
                .map(usuarioMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + id));
    }

    @Override
    public UsuarioDto save(UsuarioDto dto) {
        Usuario entity = usuarioMapper.toEntity(dto);

        if (dto.getCatalogoId() != null) {
            Catalogo catalogo = catalogoRepository.findById(dto.getCatalogoId())
                    .orElseThrow(() -> new EntityNotFoundException("Catalogo no encontrado: " + dto.getCatalogoId()));
            entity.setCatalogo(catalogo);
        }

        return usuarioMapper.toDto(usuarioRepository.save(entity));
    }

    @Override
    public UsuarioDto update(Long id, UsuarioDto dto) {
        Usuario entity = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + id));

        entity.setNombreUsuario(dto.getNombreUsuario());
        entity.setContrasena(dto.getContrasena());
        entity.setEmail(dto.getEmail());
        entity.setActivo(dto.getActivo());

        if (dto.getCatalogoId() != null) {
            Catalogo catalogo = catalogoRepository.findById(dto.getCatalogoId())
                    .orElseThrow(() -> new EntityNotFoundException("Catalogo no encontrado: " + dto.getCatalogoId()));
            entity.setCatalogo(catalogo);
        }

        return usuarioMapper.toDto(usuarioRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuario no encontrado: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}
