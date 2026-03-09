package org.example.domain.notification.domain.model;

public enum NotificationType {
    ORDER_CREATED("주문이 생성되었습니다"),
    ORDER_PAID("결제가 완료되었습니다"),
    ORDER_SHIPPED("배송이 시작되었습니다"),
    ORDER_COMPLETED("배송이 완료되었습니다"),
    ORDER_CANCELED("주문이 취소되었습니다"),
    PRODUCT_DISCOUNT("할인 이벤트가 시작되었습니다");

    private final String message;

    NotificationType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
