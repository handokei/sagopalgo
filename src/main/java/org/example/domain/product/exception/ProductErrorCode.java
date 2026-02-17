package org.example.domain.product.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.global.response.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
    PRODUCT_NOT_FOUND_EXCEPTION(HttpStatus.UNAUTHORIZED,"상품이 존재하지 않습니다." ),
    DUPLICATE_PRODUCT_TITLE(HttpStatus.UNAUTHORIZED,"동일한 상품명의 제품이 존재합니다." ),
    VALID_NON_ZERO_PRICE(HttpStatus.UNAUTHORIZED,"금액은 0원이상이여야합니다." ),
    VALID_NON_ZERO_STOCK(HttpStatus.UNAUTHORIZED,"재고는 1개 이상이여야 합니다." ),
    INVALID_OUT_OF_STOCK_STATUS_CHANGE(HttpStatus.UNAUTHORIZED,"재고를 수정하여야 상태변경이 가능합니다." ),
    VALID_NON_NULL_PRODUCT_CATEGORY(HttpStatus.BAD_REQUEST,"상품 카테고리는 필수 입력 항목입니다."),
    SELLER_REQUIRED(HttpStatus.BAD_REQUEST, "판매자 정보는 필수입니다."),
    NOT_SELLER_OF_PRODUCT(HttpStatus.FORBIDDEN, "해당 상품의 판매자가 아닙니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
    PRODUCT_NOT_ON_SALE(HttpStatus.BAD_REQUEST, "판매 중인 상품이 아닙니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "이미지를 찾을 수 없습니다.");

    private final HttpStatus status;

    private final String message;

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

}
