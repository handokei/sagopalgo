package org.example.domain.product.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.like.domain.repository.ProductLikeRepository;
import org.example.domain.product.controller.dto.*;
import org.example.domain.product.domain.model.Product;
import org.example.domain.product.domain.model.ProductCategory;
import org.example.domain.product.domain.repository.ProductRepository;
import org.example.domain.product.exception.ProductErrorCode;
import org.example.domain.product.exception.ProductException;
import org.example.domain.user.domain.repository.UserRepository;
import org.example.domain.user.exception.UserErrorCode;
import org.example.domain.user.exception.UserException;
import org.example.domain.notification.domain.model.NotificationType;
import org.example.domain.notification.service.NotificationService;
import org.example.domain.viewhistory.service.ViewHistoryService;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ViewHistoryService viewHistoryService;
    private final NotificationService notificationService;
    private final ProductLikeRepository productLikeRepository;
    private final ProductCacheService productCacheService;


    @Transactional
    public ProductCreateResponseDto createProduct(Long userId, @Valid ProductCreateRequestDto requestDto) {
        var seller = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        if (productRepository.existsByTitle(requestDto.getTitle())) {
            throw new ProductException(ProductErrorCode.DUPLICATE_PRODUCT_TITLE);
        }

        Product product = Product.of(
                seller,
                requestDto.getTitle(),
                requestDto.getContents(),
                requestDto.getPrice(),
                requestDto.getStock(),
                requestDto.getProductStatus(),
                requestDto.getProductCategory());

        productRepository.save(product);

        // 캐시 무효화
        productCacheService.evictAllProductList();

        return ProductCreateResponseDto.from(product);
    }


    public Page<ProductResponseDto> getProducts(int page, int size, String sort, ProductCategory productCategory, String keyword) {
        // 캐시 키 생성
        String cacheKey = productCacheService.generateListCacheKey(
                page, size, sort,
                productCategory != null ? productCategory.name() : null,
                keyword
        );

        Pageable pageable = PageRequest.of(page, size);

        // 캐시 조회
        var cached = productCacheService.getProductList(cacheKey);
        if (cached != null) {
            return new org.springframework.data.domain.PageImpl<>(cached, pageable, cached.size());
        }

        // DB 조회
        Page<ProductResponseDto> result = productRepository.search(
                pageable,
                sort,
                productCategory,
                keyword
        ).map(ProductResponseDto::from);

        // 캐시 저장
        productCacheService.setProductList(cacheKey, result.getContent());

        return result;
    }

    public Page<ProductResponseDto> getMyProducts(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findBySellerIdAndIsDeletedFalse(userId, pageable)
                .map(ProductResponseDto::from);
    }

    @Transactional
    public ProductResponseDto readOne(Long id, Long userId) {

        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        if (userId != null) {
            viewHistoryService.record(userId, product.getId(), product.getProductCategory());
        }

        return ProductResponseDto.from(product);
    }

    @Transactional
    public ProductResponseDto update(Long productId, Long userId, ProductUpdateRequestDto requestDto) {
        userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        product.validateSeller(userId);

        int oldPrice = product.getPrice();
        int newPrice = requestDto.getPrice();

        product.update(requestDto.getTitle(),
                requestDto.getContents(),
                requestDto.getPrice(),
                requestDto.getStock(),
                requestDto.getProductStatus(),
                requestDto.getProductCategory());

        // 가격이 낮아졌으면 (할인) 조회했던 사용자에게 알림
        if (newPrice < oldPrice) {
            sendDiscountNotification(product, oldPrice, newPrice);
        }

        // 캐시 무효화
        productCacheService.evictProduct(productId);
        productCacheService.evictAllProductList();

        return ProductResponseDto.from(product);
    }

    private void sendDiscountNotification(Product product, int oldPrice, int newPrice) {
        // 조회한 사용자 + 찜한 사용자 (중복 제거)
        Set<Long> userIds = Stream.concat(
                viewHistoryService.findUsersByProductId(product.getId()).stream(),
                productLikeRepository.findUserIdsByProductId(product.getId()).stream()
        ).collect(Collectors.toSet());

        int discountPercent = (int) ((1 - (double) newPrice / oldPrice) * 100);
        String message = String.format("'%s' 상품이 %d%% 할인 중!",
                product.getTitle(), discountPercent);

        userIds.forEach(targetUserId ->
                notificationService.send(targetUserId, NotificationType.PRODUCT_DISCOUNT, message, product.getId())
        );
    }

    @Transactional
    public void deleted(Long productId, Long userId) {
        userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        product.validateSeller(userId);

        product.delete();

        // 캐시 무효화
        productCacheService.evictProduct(productId);
        productCacheService.evictAllProductList();
    }

    @Transactional
    public ProductResponseDto updateStatus(Long productId, Long userId, ProductStatusUpdateRequestDto requestDto) {
        userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        product.validateSeller(userId);

        product.updateProductStatus(requestDto.getProductStatus());

        // 캐시 무효화
        productCacheService.evictProduct(productId);
        productCacheService.evictAllProductList();

        return ProductResponseDto.from(product);
    }
}
