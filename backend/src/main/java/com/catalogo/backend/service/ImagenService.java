package com.catalogo.backend.service;

import com.catalogo.backend.dto.ImagenDto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface ImagenService {
    List<ImagenDto> findAll();

    ImagenDto findById(Long id);

    ImagenDto save(ImagenDto dto);

    ImagenDto upload(MultipartFile file, Long productoId, String nombre,
            Boolean esPrincipal, Integer orden);

    ImagenDto update(Long id, ImagenDto dto);

    void deleteById(Long id);
}
