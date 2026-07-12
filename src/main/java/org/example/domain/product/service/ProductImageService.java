package org.example.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.product.controller.dto.ProductImageResponseDto;
import org.example.domain.product.domain.model.Product;
import org.example.domain.product.domain.model.ProductImage;
import org.example.domain.product.domain.repository.ProductImageRepository;
import org.example.domain.product.domain.repository.ProductRepository;
import org.example.domain.product.exception.ProductErrorCode;
import org.example.domain.product.exception.ProductException;
import org.example.global.file.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public List<ProductImageResponseDto> uploadImages(Long userId, Long productId, List<MultipartFile> files) {
        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        product.validateSeller(userId);

        int currentCount = productImageRepository.countByProductIdAndIsDeletedFalse(productId);
        boolean hasMainImage = productImageRepository.findByProductIdAndIsMainTrueAndIsDeletedFalse(productId).isPresent();

        List<ProductImageResponseDto> result = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String storedFileName = fileStorageService.store(file);
            boolean isMain = !hasMainImage && i == 0;

            ProductImage image = ProductImage.of(
                    productId,
                    storedFileName,
                    file.getOriginalFilename(),
                    currentCount + i,
                    isMain
            );

            productImageRepository.save(image);
            result.add(ProductImageResponseDto.from(image, fileStorageService.getFileUrl(storedFileName)));

            if (isMain) {
                hasMainImage = true;
            }
        }

        return result;
    }

    @Transactional
    public void deleteImage(Long userId, Long productId, Long imageId) {
        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        product.validateSeller(userId);

        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.IMAGE_NOT_FOUND));

        if (!image.getProductId().equals(productId)) {
            throw new ProductException(ProductErrorCode.IMAGE_NOT_FOUND);
        }

        image.delete();
    }

    public List<ProductImageResponseDto> getImages(Long productId) {
        List<ProductImage> images = productImageRepository.findByProductIdAndIsDeletedFalseOrderBySortOrderAsc(productId);
        return images.stream()
                .map(image -> ProductImageResponseDto.from(image, fileStorageService.getFileUrl(image.getImageUrl())))
                .toList();
    }
}
