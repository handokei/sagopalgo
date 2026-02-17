package org.example.domain.like.controller;

import lombok.RequiredArgsConstructor;
import org.example.domain.like.controller.dto.ProductLikeResponseDto;
import org.example.domain.like.controller.dto.ProductLikesCreateResponseDto;
import org.example.domain.like.service.ProductLikeService;
import org.example.global.security.jwt.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/products")
@RequiredArgsConstructor
@RestController
public class ProductLikeController {

    private final ProductLikeService  productLikeService;


    @PostMapping("/{id}/likes")
    public ResponseEntity<ProductLikesCreateResponseDto> create(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {

        ProductLikesCreateResponseDto responseDto = productLikeService.createLike(id, userDetails.getId());
        return ResponseEntity.ok().body(responseDto);

    }

    @GetMapping("/{id}/likes/check")
    public ResponseEntity<Map<String, Boolean>> checkLike(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        boolean liked = productLikeService.isLiked(id, userDetails.getId());
        return ResponseEntity.ok(Map.of("liked", liked));
    }

    @GetMapping("/me/likes")
    public ResponseEntity<Page<ProductLikeResponseDto>> getProductLikes(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProductLikeResponseDto> responseDto = productLikeService.getProductLikes(userDetails.getId(), page, size);

        return ResponseEntity.ok().body(responseDto);
    }


}
