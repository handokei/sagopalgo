package org.example.domain.product.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductStatus {
    ON_SALE("판매중"),
    DISCONTINUE("판매중단"),
    OUT_OF_STOCK("품절");

    private final String productStatus;

}
