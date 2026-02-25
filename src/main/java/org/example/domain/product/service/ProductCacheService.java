package org.example.domain.product.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.product.controller.dto.ProductResponseDto;
import org.example.domain.product.domain.model.ProductStatus;
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
    private static final String PRODUCT_DETAIL_KEY = "products:detail";
    private static final long PRODUCT_LIST_TTL = 5;
    private static final long PRODUCT_DETAIL_TTL = 10;

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

    public ProductResponseDto getProduct(Long productId) {
        try {
            Object cached = redisTemplate.opsForValue().get(PRODUCT_DETAIL_KEY + ":" + productId);
            if (cached != null) {
                log.debug("Cache HIT: product:{}", productId);
                return redisObjectMapper.convertValue(cached, ProductResponseDto.class);
            }
        } catch (Exception e) {
            log.warn("Cache read failed: {}", e.getMessage());
        }
        return null;
    }

    public void setProduct(Long productId, ProductResponseDto product) {
        try {
            redisTemplate.opsForValue().set(
                    PRODUCT_DETAIL_KEY + ":" + productId,
                    product,
                    PRODUCT_DETAIL_TTL,
                    TimeUnit.MINUTES
            );
            log.debug("Cache SET: product:{}", productId);
        } catch (Exception e) {
            log.warn("Cache write failed: {}", e.getMessage());
        }
    }

    public void evictProduct(Long productId) {
        try {
            redisTemplate.delete(PRODUCT_DETAIL_KEY + ":" + productId);
            log.debug("Cache EVICT: product:{}", productId);
        } catch (Exception e) {
            log.warn("Cache evict failed: {}", e.getMessage());
        }
    }

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

    public String generateListCacheKey(int page, int size, String sort, Long categoryId,
                                       String keyword, Integer minPrice, Integer maxPrice, ProductStatus status) {
        return String.format("p=%d:sz=%d:sort=%s:cat=%s:kw=%s:min=%s:max=%s:st=%s",
                page, size,
                sort != null ? sort : "default",
                categoryId != null ? categoryId : "all",
                keyword != null ? keyword : "none",
                minPrice != null ? minPrice : "none",
                maxPrice != null ? maxPrice : "none",
                status != null ? status.name() : "none"
        );
    }
}
