package com.catalogo.backend.controller;

import com.catalogo.backend.dto.PaginaDto;
import com.catalogo.backend.service.PaginaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/paginas")
public class PaginaController {

    private final PaginaService paginaService;

    public PaginaController(PaginaService paginaService) {
        this.paginaService = paginaService;
    }

    @GetMapping
    public List<PaginaDto> findAll() {
        return paginaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaginaDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(paginaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PaginaDto> save(@Valid @RequestBody PaginaDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paginaService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaginaDto> update(@PathVariable Long id, @Valid @RequestBody PaginaDto dto) {
        return ResponseEntity.ok(paginaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paginaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
