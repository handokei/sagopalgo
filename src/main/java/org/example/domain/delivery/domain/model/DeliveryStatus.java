package org.example.domain.delivery.domain.model;

public enum DeliveryStatus {
    PENDING,      // 배송 대기
    PREPARING,    // 배송 준비중
    SHIPPED,      // 배송중
    DELIVERED,    // 배송 완료
    CANCELED      // 배송 취소
}
