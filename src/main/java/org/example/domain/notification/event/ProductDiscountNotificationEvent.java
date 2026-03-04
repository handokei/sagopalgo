package org.example.domain.notification.event;

import lombok.Getter;

import java.util.List;

@Getter
public class ProductDiscountNotificationEvent {

    private final List<Long> userIds;
    private final String message;
    private final Long productId;

    public ProductDiscountNotificationEvent(List<Long> userIds, String message, Long productId) {
        this.userIds = userIds;
        this.message = message;
        this.productId = productId;
    }
}
