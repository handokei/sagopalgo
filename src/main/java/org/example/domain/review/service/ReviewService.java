package org.example.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.product.domain.repository.ProductRepository;
import org.example.domain.product.exception.ProductErrorCode;
import org.example.domain.product.exception.ProductException;
import org.example.domain.review.controller.dto.ReviewCreateRequestDto;
import org.example.domain.review.controller.dto.ReviewResponseDto;
import org.example.domain.review.controller.dto.ReviewSummaryResponseDto;
import org.example.domain.review.controller.dto.ReviewUpdateRequestDto;
import org.example.domain.review.domain.model.Review;
import org.example.domain.review.domain.repository.ReviewRepository;
import org.example.domain.review.exception.ReviewErrorCode;
import org.example.domain.review.exception.ReviewException;
import org.example.domain.user.domain.repository.UserRepository;
import org.example.domain.user.exception.UserErrorCode;
import org.example.domain.user.exception.UserException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewResponseDto create(Long userId, Long productId, ReviewCreateRequestDto requestDto) {
        userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND_EXCEPTION));

        productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        if (reviewRepository.existsByUserIdAndProductIdAndIsDeletedFalse(userId, productId)) {
            throw new ReviewException(ReviewErrorCode.ALREADY_REVIEWED);
        }

        Review review = Review.of(userId, productId, requestDto.getRating(), requestDto.getContents());
        reviewRepository.save(review);

        return ReviewResponseDto.from(review);
    }

    public Page<ReviewResponseDto> getByProductId(Long productId, int page, int size) {
        productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        return reviewRepository.findByProductIdAndIsDeletedFalseOrderByCreatedAtDesc(productId, PageRequest.of(page, size))
                .map(ReviewResponseDto::from);
    }

    public ReviewSummaryResponseDto getSummary(Long productId) {
        productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND_EXCEPTION));

        Object[] result = reviewRepository.findRatingSummaryByProductId(productId);
        Double avgRating = (Double) result[0];
        Long count = (Long) result[1];

        return ReviewSummaryResponseDto.of(avgRating, count);
    }

    public Page<ReviewResponseDto> getMyReviews(Long userId, int page, int size) {
        return reviewRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .map(ReviewResponseDto::from);
    }

    @Transactional
    public ReviewResponseDto update(Long userId, Long reviewId, ReviewUpdateRequestDto requestDto) {
        Review review = reviewRepository.findByIdAndIsDeletedFalse(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        review.validateOwner(userId);
        review.update(requestDto.getRating(), requestDto.getContents());

        return ReviewResponseDto.from(review);
    }

    @Transactional
    public void delete(Long userId, Long reviewId) {
        Review review = reviewRepository.findByIdAndIsDeletedFalse(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND));

        review.validateOwner(userId);
        review.delete();
    }
}
