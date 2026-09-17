package com.catalogo.backend.controller;

import com.catalogo.backend.dto.ImagenDto;
import com.catalogo.backend.service.ImagenService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/imagenes")
public class ImagenController {

    private final ImagenService imagenService;

    public ImagenController(ImagenService imagenService) {
        this.imagenService = imagenService;
    }

    @GetMapping
    public List<ImagenDto> findAll() {
        return imagenService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImagenDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(imagenService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ImagenDto> save(@Valid @RequestBody ImagenDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(imagenService.save(dto));
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<ImagenDto> upload(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "productoId", required = false) Long productoId,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "esPrincipal", defaultValue = "false") Boolean esPrincipal,
            @RequestParam(value = "orden", required = false) Integer orden) {
        log.info("POST /api/imagenes/upload productoId={} nombre={}", productoId, nombre);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(imagenService.upload(file, productoId, nombre, esPrincipal, orden));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ImagenDto> update(@PathVariable Long id, @Valid @RequestBody ImagenDto dto) {
        return ResponseEntity.ok(imagenService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        imagenService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
