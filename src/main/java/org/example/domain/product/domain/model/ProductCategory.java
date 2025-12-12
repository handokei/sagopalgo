package org.example.domain.product.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductCategory {

    T_SHIRT("티셔츠"),
    HOODIE("후드"),
    OUTER("아우터"),
    PANTS("바지"),
    SHOES("신발");

    private final String productCategory;

}
