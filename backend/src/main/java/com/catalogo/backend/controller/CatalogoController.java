package com.catalogo.backend.controller;

import com.catalogo.backend.dto.CatalogoDto;
import com.catalogo.backend.service.CatalogoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/catalogos")
public class CatalogoController {

    private final CatalogoService catalogoService;

    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping
    public List<CatalogoDto> findAll() {
        return catalogoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CatalogoDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CatalogoDto> save(@Valid @RequestBody CatalogoDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogoService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CatalogoDto> update(@PathVariable Long id, @Valid @RequestBody CatalogoDto dto) {
        return ResponseEntity.ok(catalogoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        catalogoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
