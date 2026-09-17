package com.catalogo.backend.mapper;

import com.catalogo.backend.dto.RolDto;
import com.catalogo.backend.entity.Rol;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class RolMapper {
    public RolDto toDto(Rol entity) {
        if (entity == null)
            return null;
        RolDto dto = new RolDto();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setUsuarioRolIds(entity.getUsuarios() == null ? Collections.emptyList()
                : entity.getUsuarios().stream().map(usuarioRol -> usuarioRol.getId()).toList());
        return dto;
    }

    public Rol toEntity(RolDto dto) {
        if (dto == null)
            return null;
        Rol entity = new Rol();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        return entity;
    }
}
