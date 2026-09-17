package com.catalogo.backend.service;

import com.catalogo.backend.dto.ConfiguracionWhatsAppDto;
import com.catalogo.backend.entity.Catalogo;
import com.catalogo.backend.entity.ConfiguracionWhatsApp;
import com.catalogo.backend.mapper.ConfiguracionWhatsAppMapper;
import com.catalogo.backend.repository.CatalogoRepository;
import com.catalogo.backend.repository.ConfiguracionWhatsAppRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class ConfiguracionWhatsAppServiceImpl implements ConfiguracionWhatsAppService {

    private final ConfiguracionWhatsAppRepository configuracionWhatsAppRepository;
    private final CatalogoRepository catalogoRepository;
    private final ConfiguracionWhatsAppMapper configuracionWhatsAppMapper;

    public ConfiguracionWhatsAppServiceImpl(ConfiguracionWhatsAppRepository configuracionWhatsAppRepository,
            CatalogoRepository catalogoRepository,
            ConfiguracionWhatsAppMapper configuracionWhatsAppMapper) {
        this.configuracionWhatsAppRepository = configuracionWhatsAppRepository;
        this.catalogoRepository = catalogoRepository;
        this.configuracionWhatsAppMapper = configuracionWhatsAppMapper;
    }

    @Override
    public List<ConfiguracionWhatsAppDto> findAll() {
        return configuracionWhatsAppRepository.findAll().stream()
                .map(configuracionWhatsAppMapper::toDto)
                .toList();
    }

    @Override
    public ConfiguracionWhatsAppDto findById(Long id) {
        return configuracionWhatsAppRepository.findById(id)
                .map(configuracionWhatsAppMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Configuración WhatsApp no encontrada: " + id));
    }

    @Override
    public ConfiguracionWhatsAppDto save(ConfiguracionWhatsAppDto dto) {
        ConfiguracionWhatsApp entity = configuracionWhatsAppMapper.toEntity(dto);

        if (dto.getCatalogoId() != null) {
            Catalogo catalogo = catalogoRepository.findById(dto.getCatalogoId())
                    .orElseThrow(() -> new EntityNotFoundException("Catalogo no encontrado: " + dto.getCatalogoId()));
            entity.setCatalogo(catalogo);
        }

        return configuracionWhatsAppMapper.toDto(configuracionWhatsAppRepository.save(entity));
    }

    @Override
    public ConfiguracionWhatsAppDto update(Long id, ConfiguracionWhatsAppDto dto) {
        ConfiguracionWhatsApp entity = configuracionWhatsAppRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Configuración WhatsApp no encontrada: " + id));

        entity.setNumero(dto.getNumero());
        entity.setMensajePersonalizado(dto.getMensajePersonalizado());
        entity.setActiva(dto.getActiva());

        if (dto.getCatalogoId() != null) {
            Catalogo catalogo = catalogoRepository.findById(dto.getCatalogoId())
                    .orElseThrow(() -> new EntityNotFoundException("Catalogo no encontrado: " + dto.getCatalogoId()));
            entity.setCatalogo(catalogo);
        }

        return configuracionWhatsAppMapper.toDto(configuracionWhatsAppRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        if (!configuracionWhatsAppRepository.existsById(id)) {
            throw new EntityNotFoundException("Configuración WhatsApp no encontrada: " + id);
        }
        configuracionWhatsAppRepository.deleteById(id);
    }
}
