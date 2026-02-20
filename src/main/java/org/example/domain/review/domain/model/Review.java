package org.example.domain.review.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.domain.review.exception.ReviewErrorCode;
import org.example.domain.review.exception.ReviewException;
import org.example.global.config.entity.BaseEntity;

@Entity
@Table(name = "reviews", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "product_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private int rating;

    @Column(length = 500)
    private String contents;

    private boolean isDeleted = false;

    private Review(Long userId, Long productId, int rating, String contents) {
        if (rating < 1 || rating > 5) {
            throw new ReviewException(ReviewErrorCode.INVALID_RATING);
        }
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.contents = contents;
    }

    public static Review of(Long userId, Long productId, int rating, String contents) {
        return new Review(userId, productId, rating, contents);
    }

    public void validateOwner(Long userId) {
        if (!this.userId.equals(userId)) {
            throw new ReviewException(ReviewErrorCode.NOT_REVIEW_OWNER);
        }
    }

    public void update(int rating, String contents) {
        if (rating < 1 || rating > 5) {
            throw new ReviewException(ReviewErrorCode.INVALID_RATING);
        }
        this.rating = rating;
        if (contents != null) {
            this.contents = contents;
        }
    }

    public void delete() {
        this.isDeleted = true;
    }
}
