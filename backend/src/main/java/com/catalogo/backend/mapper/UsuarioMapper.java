package com.catalogo.backend.mapper;

import com.catalogo.backend.dto.UsuarioDto;
import com.catalogo.backend.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class UsuarioMapper {
    public UsuarioDto toDto(Usuario entity) {
        if (entity == null)
            return null;
        UsuarioDto dto = new UsuarioDto();
        dto.setId(entity.getId());
        dto.setNombreUsuario(entity.getNombreUsuario());
        dto.setContrasena(entity.getContrasena());
        dto.setEmail(entity.getEmail());
        dto.setActivo(entity.getActivo());
        dto.setFechaCreacion(entity.getFechaCreacion());
        dto.setUltimoAcceso(entity.getUltimoAcceso());
        dto.setCatalogoId(entity.getCatalogo() == null ? null : entity.getCatalogo().getId());
        dto.setRolIds(entity.getRoles() == null ? Collections.emptyList()
                : entity.getRoles().stream().map(usuarioRol -> usuarioRol.getRol().getId()).toList());
        return dto;
    }

    public Usuario toEntity(UsuarioDto dto) {
        if (dto == null)
            return null;
        Usuario entity = new Usuario();
        entity.setNombreUsuario(dto.getNombreUsuario());
        entity.setContrasena(dto.getContrasena());
        entity.setEmail(dto.getEmail());
        entity.setActivo(dto.getActivo());
        return entity;
    }
}
