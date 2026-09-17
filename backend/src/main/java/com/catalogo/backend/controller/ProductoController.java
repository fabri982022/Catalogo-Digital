package com.catalogo.backend.controller;

import com.catalogo.backend.dto.ProductoDto;
import com.catalogo.backend.service.ProductoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<ProductoDto> findAll() {
        log.info("GET /api/productos");
        return productoService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDto> findById(@PathVariable Long id) {
        log.info("GET /api/productos/{}", id);
        return ResponseEntity.ok(productoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductoDto> save(@Valid @RequestBody ProductoDto dto) {
        log.info("POST /api/productos codigo={} nombre={}", dto.getCodigo(), dto.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDto> update(@PathVariable Long id, @Valid @RequestBody ProductoDto dto) {
        log.info("PUT /api/productos/{} codigo={}", id, dto.getCodigo());
        return ResponseEntity.ok(productoService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/productos/{}", id);
        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
