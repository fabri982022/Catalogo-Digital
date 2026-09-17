package com.catalogo.backend.controller;

import com.catalogo.backend.dto.ConfiguracionWhatsAppDto;
import com.catalogo.backend.service.ConfiguracionWhatsAppService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/configuraciones-whatsapp")
public class ConfiguracionWhatsAppController {

    private final ConfiguracionWhatsAppService configuracionWhatsAppService;

    public ConfiguracionWhatsAppController(ConfiguracionWhatsAppService configuracionWhatsAppService) {
        this.configuracionWhatsAppService = configuracionWhatsAppService;
    }

    @GetMapping
    public List<ConfiguracionWhatsAppDto> findAll() {
        return configuracionWhatsAppService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConfiguracionWhatsAppDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(configuracionWhatsAppService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ConfiguracionWhatsAppDto> save(@Valid @RequestBody ConfiguracionWhatsAppDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(configuracionWhatsAppService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConfiguracionWhatsAppDto> update(@PathVariable Long id,
            @Valid @RequestBody ConfiguracionWhatsAppDto dto) {
        return ResponseEntity.ok(configuracionWhatsAppService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        configuracionWhatsAppService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
