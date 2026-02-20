package org.example.domain.review.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewSummaryResponseDto {

    private Double averageRating;
    private Long reviewCount;

    public static ReviewSummaryResponseDto of(Double averageRating, Long reviewCount) {
        return new ReviewSummaryResponseDto(
                averageRating != null ? Math.round(averageRating * 10) / 10.0 : 0.0,
                reviewCount != null ? reviewCount : 0L
        );
    }
}
