package org.example.domain.product.controller;

import lombok.RequiredArgsConstructor;
import org.example.domain.product.controller.dto.ProductImageResponseDto;
import org.example.domain.product.service.ProductImageService;
import org.example.global.file.LocalFileStorageService;
import org.example.global.security.jwt.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    @Autowired(required = false)
    private LocalFileStorageService localFileStorageService;

    @PostMapping("/{productId}/images")
    public ResponseEntity<List<ProductImageResponseDto>> uploadImages(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId,
            @RequestParam("files") List<MultipartFile> files) {
        Long userId = userDetails.getId();
        List<ProductImageResponseDto> images = productImageService.uploadImages(userId, productId, files);
        return ResponseEntity.status(201).body(images);
    }

    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId,
            @PathVariable Long imageId) {
        Long userId = userDetails.getId();
        productImageService.deleteImage(userId, productId, imageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}/images")
    public ResponseEntity<List<ProductImageResponseDto>> getImages(@PathVariable Long productId) {
        List<ProductImageResponseDto> images = productImageService.getImages(productId);
        return ResponseEntity.ok(images);
    }

    @GetMapping("/images/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        if (localFileStorageService == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            Path filePath = localFileStorageService.getFilePath(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                MediaType mediaType = getMediaType(fileName);
                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private MediaType getMediaType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "png" -> MediaType.IMAGE_PNG;
            case "gif" -> MediaType.IMAGE_GIF;
            case "webp" -> MediaType.parseMediaType("image/webp");
            default -> MediaType.IMAGE_JPEG;
        };
    }
}
