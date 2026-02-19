package org.example.domain.product.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.product.controller.dto.ProductResponseDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper;

    private static final String PRODUCT_LIST_KEY = "products:list";
    private static final String PRODUCT_KEY_PREFIX = "products:";
    private static final long PRODUCT_LIST_TTL = 5;  // 5분
    private static final long PRODUCT_DETAIL_TTL = 10;  // 10분

    /**
     * 상품 목록 캐시 조회
     */
    public List<ProductResponseDto> getProductList(String cacheKey) {
        try {
            Object cached = redisTemplate.opsForValue().get(PRODUCT_LIST_KEY + ":" + cacheKey);
            if (cached != null) {
                log.debug("Cache HIT: {}", cacheKey);
                return redisObjectMapper.convertValue(cached, new TypeReference<List<ProductResponseDto>>() {});
            }
        } catch (Exception e) {
            log.warn("Cache read failed: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 상품 목록 캐시 저장
     */
    public void setProductList(String cacheKey, List<ProductResponseDto> products) {
        try {
            redisTemplate.opsForValue().set(
                    PRODUCT_LIST_KEY + ":" + cacheKey,
                    products,
                    PRODUCT_LIST_TTL,
                    TimeUnit.MINUTES
            );
            log.debug("Cache SET: {}", cacheKey);
        } catch (Exception e) {
            log.warn("Cache write failed: {}", e.getMessage());
        }
    }

    /**
     * 상품 상세 캐시 조회
     */
    public ProductResponseDto getProduct(Long productId) {
        try {
            Object cached = redisTemplate.opsForValue().get(PRODUCT_KEY_PREFIX + productId);
            if (cached != null) {
                log.debug("Cache HIT: product:{}", productId);
                return redisObjectMapper.convertValue(cached, ProductResponseDto.class);
            }
        } catch (Exception e) {
            log.warn("Cache read failed: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 상품 상세 캐시 저장
     */
    public void setProduct(Long productId, ProductResponseDto product) {
        try {
            redisTemplate.opsForValue().set(
                    PRODUCT_KEY_PREFIX + productId,
                    product,
                    PRODUCT_DETAIL_TTL,
                    TimeUnit.MINUTES
            );
            log.debug("Cache SET: product:{}", productId);
        } catch (Exception e) {
            log.warn("Cache write failed: {}", e.getMessage());
        }
    }

    /**
     * 상품 캐시 삭제 (수정/삭제 시)
     */
    public void evictProduct(Long productId) {
        try {
            redisTemplate.delete(PRODUCT_KEY_PREFIX + productId);
            log.debug("Cache EVICT: product:{}", productId);
        } catch (Exception e) {
            log.warn("Cache evict failed: {}", e.getMessage());
        }
    }

    /**
     * 상품 목록 캐시 전체 삭제
     */
    public void evictAllProductList() {
        try {
            var keys = redisTemplate.keys(PRODUCT_LIST_KEY + ":*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("Cache EVICT ALL: product list ({} keys)", keys.size());
            }
        } catch (Exception e) {
            log.warn("Cache evict all failed: {}", e.getMessage());
        }
    }

    /**
     * 캐시 키 생성
     */
    public String generateListCacheKey(int page, int size, String sort, String category, String keyword) {
        return String.format("%d:%d:%s:%s:%s",
                page, size,
                sort != null ? sort : "default",
                category != null ? category : "all",
                keyword != null ? keyword : "none"
        );
    }
}
