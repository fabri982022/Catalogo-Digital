package com.catalogo.backend.controller;

import com.catalogo.backend.dto.RolDto;
import com.catalogo.backend.service.RolService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    public List<RolDto> findAll() {
        return rolService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(rolService.findById(id));
    }

    @PostMapping
    public ResponseEntity<RolDto> save(@Valid @RequestBody RolDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rolService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolDto> update(@PathVariable Long id, @Valid @RequestBody RolDto dto) {
        return ResponseEntity.ok(rolService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rolService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
