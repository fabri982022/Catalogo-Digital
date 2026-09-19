package com.catalogo.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.catalogo.backend.dto.ImagenDto;
import com.catalogo.backend.entity.Imagen;
import com.catalogo.backend.entity.Producto;
import com.catalogo.backend.mapper.ImagenMapper;
import com.catalogo.backend.repository.ImagenRepository;
import com.catalogo.backend.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.List;

@Service
@Slf4j
@Transactional
public class ImagenServiceImpl implements ImagenService {

    private static final String IMAGEN_NO_ENCONTRADA = "Imagen no encontrada: ";
    private static final String PRODUCTO_NO_ENCONTRADO = "Producto no encontrado: ";

    private final ImagenRepository imagenRepository;
    private final ProductoRepository productoRepository;
    private final ImagenMapper imagenMapper;
    private final Cloudinary cloudinary;

    public ImagenServiceImpl(ImagenRepository imagenRepository,
            ProductoRepository productoRepository,
            ImagenMapper imagenMapper,
            Cloudinary cloudinary) {
        this.imagenRepository = imagenRepository;
        this.productoRepository = productoRepository;
        this.imagenMapper = imagenMapper;
        this.cloudinary = cloudinary;
    }

    @Override
    public List<ImagenDto> findAll() {
        return imagenRepository.findAll().stream()
                .map(imagenMapper::toDto)
                .toList();
    }

    @Override
    public ImagenDto findById(Long id) {
        return imagenRepository.findById(id)
                .map(imagenMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(IMAGEN_NO_ENCONTRADA + id));
    }

    @Override
    public ImagenDto save(ImagenDto dto) {
        Imagen entity = imagenMapper.toEntity(dto);

        if (dto.getProductoId() != null) {
            Producto producto = productoRepository.findById(dto.getProductoId())
                    .orElseThrow(() -> new EntityNotFoundException(PRODUCTO_NO_ENCONTRADO + dto.getProductoId()));
            entity.setProducto(producto);
        }

        return imagenMapper.toDto(imagenRepository.save(entity));
    }

    @Override
    public ImagenDto upload(MultipartFile file, Long productoId, String nombre,
            Boolean esPrincipal, Integer orden) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo es obligatorio");
        }
        if (productoId == null) {
            throw new IllegalArgumentException("El productoId es obligatorio");
        }

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new EntityNotFoundException(
                        PRODUCTO_NO_ENCONTRADO + productoId));

        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "catalogo-digital/productos",
                            "resource_type", "image"));

            Imagen imagen = new Imagen();
            imagen.setUrl((String) result.get("secure_url"));
            imagen.setPublicId((String) result.get("public_id"));
            imagen.setNombre(nombre != null ? nombre : file.getOriginalFilename());
            imagen.setEsPrincipal(esPrincipal);
            imagen.setOrden(orden);
            imagen.setProducto(producto);

            if (Boolean.TRUE.equals(esPrincipal)) {
                producto.getImagenes().forEach(existingImage -> existingImage.setEsPrincipal(false));
            }

            return imagenMapper.toDto(imagenRepository.save(imagen));
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "No se pudo subir la imagen a Cloudinary", exception);
        }
    }

    @Override
    public ImagenDto update(Long id, ImagenDto dto) {
        Imagen entity = imagenRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(IMAGEN_NO_ENCONTRADA + id));

        entity.setUrl(dto.getUrl());
        entity.setNombre(dto.getNombre());
        entity.setEsPrincipal(dto.getEsPrincipal());
        entity.setOrden(dto.getOrden());

        if (dto.getProductoId() != null) {
            Producto producto = productoRepository.findById(dto.getProductoId())
                    .orElseThrow(() -> new EntityNotFoundException(PRODUCTO_NO_ENCONTRADO + dto.getProductoId()));
            entity.setProducto(producto);
        }

        return imagenMapper.toDto(imagenRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        Imagen imagen = imagenRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(IMAGEN_NO_ENCONTRADA + id));

        if (imagen.getPublicId() != null && !imagen.getPublicId().isBlank()) {
            try {
                cloudinary.uploader().destroy(imagen.getPublicId(),
                        ObjectUtils.asMap("resource_type", "image"));
            } catch (IOException exception) {
                throw new IllegalStateException(
                        "No se pudo eliminar la imagen de Cloudinary", exception);
            }
        }

        imagenRepository.delete(imagen);
    }
}
