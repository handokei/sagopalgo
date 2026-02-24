package org.example.domain.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.review.controller.dto.ReviewCreateRequestDto;
import org.example.domain.review.controller.dto.ReviewResponseDto;
import org.example.domain.review.controller.dto.ReviewSummaryResponseDto;
import org.example.domain.review.controller.dto.ReviewUpdateRequestDto;
import org.example.domain.review.service.ReviewService;
import org.example.global.security.jwt.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/api/products/{productId}/reviews")
    public ResponseEntity<ReviewResponseDto> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId,
            @RequestBody @Valid ReviewCreateRequestDto requestDto
    ) {
        ReviewResponseDto responseDto = reviewService.create(userDetails.getId(), productId, requestDto);
        return ResponseEntity.status(201).body(responseDto);
    }

    @GetMapping("/api/products/{productId}/reviews")
    public ResponseEntity<Page<ReviewResponseDto>> getByProductId(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ReviewResponseDto> responseDto = reviewService.getByProductId(productId, page, size);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/api/products/{productId}/reviews/summary")
    public ResponseEntity<ReviewSummaryResponseDto> getSummary(@PathVariable Long productId) {
        ReviewSummaryResponseDto responseDto = reviewService.getSummary(productId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/api/reviews/me")
    public ResponseEntity<Page<ReviewResponseDto>> getMyReviews(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ReviewResponseDto> responseDto = reviewService.getMyReviews(userDetails.getId(), page, size);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/api/reviews/{id}")
    public ResponseEntity<ReviewResponseDto> update(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @RequestBody @Valid ReviewUpdateRequestDto requestDto
    ) {
        ReviewResponseDto responseDto = reviewService.update(userDetails.getId(), id, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/api/reviews/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        reviewService.delete(userDetails.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
