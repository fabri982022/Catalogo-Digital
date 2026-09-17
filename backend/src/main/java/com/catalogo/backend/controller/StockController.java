package com.catalogo.backend.controller;

import com.catalogo.backend.dto.StockDto;
import com.catalogo.backend.service.StockService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public List<StockDto> findAll() {
        return stockService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(stockService.findById(id));
    }

    @PostMapping
    public ResponseEntity<StockDto> save(@Valid @RequestBody StockDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockDto> update(@PathVariable Long id, @Valid @RequestBody StockDto dto) {
        return ResponseEntity.ok(stockService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stockService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
