package com.catalogo.backend.mapper;

import com.catalogo.backend.dto.UsuarioRolDto;
import com.catalogo.backend.entity.UsuarioRol;
import org.springframework.stereotype.Component;

@Component
public class UsuarioRolMapper {
    public UsuarioRolDto toDto(UsuarioRol entity) {
        if (entity == null)
            return null;
        UsuarioRolDto dto = new UsuarioRolDto();
        dto.setId(entity.getId());
        dto.setUsuarioId(entity.getUsuario() == null ? null : entity.getUsuario().getId());
        dto.setRolId(entity.getRol() == null ? null : entity.getRol().getId());
        return dto;
    }

    public UsuarioRol toEntity(UsuarioRolDto dto) {
        if (dto == null)
            return null;
        UsuarioRol entity = new UsuarioRol();
        return new UsuarioRol();
    }
}
