package org.example.domain.product.domain.model;

public enum StockStatus {
    IN_STOCK,      // 재고 충분 (5개 이상)
    LOW_STOCK,     // 품절 임박 (1~4개)
    OUT_OF_STOCK   // 품절 (0개)
}
