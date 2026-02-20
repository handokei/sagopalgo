package org.example.domain.review.controller.dto;

import lombok.Getter;
import org.example.domain.review.domain.model.Review;

import java.time.LocalDateTime;

@Getter
public class ReviewResponseDto {

    private Long id;
    private Long userId;
    private Long productId;
    private int rating;
    private String contents;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.userId = review.getUserId();
        this.productId = review.getProductId();
        this.rating = review.getRating();
        this.contents = review.getContents();
        this.createdAt = review.getCreatedAt();
        this.updatedAt = review.getUpdatedAt();
    }

    public static ReviewResponseDto from(Review review) {
        return new ReviewResponseDto(review);
    }
}
