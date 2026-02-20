package org.example.domain.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.product.controller.dto.*;
import org.example.domain.product.service.ProductService;

import org.example.global.security.jwt.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    //제품 생성
    @PostMapping
    public ResponseEntity<ProductCreateResponseDto> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid ProductCreateRequestDto requestDto
            ) {
        Long userId = userDetails.getId();
        ProductCreateResponseDto responseDto = productService.createProduct(userId, requestDto);
        return ResponseEntity.ok().body(responseDto);
    }
    //다건 조회
    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword
    ) {
        Page<ProductResponseDto> responseDto = productService.getProducts(page, size, sort, categoryId, keyword);
        return ResponseEntity.ok().body(responseDto);
    }

    //내 상품 조회
    @GetMapping("/me")
    public ResponseEntity<Page<ProductResponseDto>> getMyProducts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Long userId = userDetails.getId();
        Page<ProductResponseDto> responseDto = productService.getMyProducts(userId, page, size);
        return ResponseEntity.ok().body(responseDto);
    }

    //단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> readOneProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails != null ? userDetails.getId() : null;
        ProductResponseDto responseDto = productService.readOne(id, userId);

        return ResponseEntity.ok().body(responseDto);
    }

    //제품 수정
    @PatchMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid ProductUpdateRequestDto requestDto

    ) {
        ProductResponseDto responseDto = productService.update(productId, userDetails.getId(), requestDto);

        return ResponseEntity.ok().body(responseDto);
    }


    //제품 상태 수정
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProductResponseDto> updateStatusProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid ProductStatusUpdateRequestDto requestDto
    ) {
        Long userId = userDetails.getId();
        ProductResponseDto responseDto = productService.updateStatus(id,
                userId,
                requestDto);

       return ResponseEntity.ok().body(responseDto);

    }

    //제품 삭제
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deletedProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        productService.deleted(productId,userDetails.getId());

        return ResponseEntity.ok(null);
    }
}
