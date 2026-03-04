package org.example.domain.notification.event;

import lombok.Getter;
import org.example.domain.notification.domain.model.NotificationType;

@Getter
public class OrderNotificationEvent {

    private final Long userId;
    private final NotificationType type;
    private final String message;
    private final Long referenceId;

    public OrderNotificationEvent(Long userId, NotificationType type, String message, Long referenceId) {
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.referenceId = referenceId;
    }
}
