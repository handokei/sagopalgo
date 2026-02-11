package org.example.domain.notification.controller.dto;

import lombok.Getter;
import org.example.domain.notification.domain.model.Notification;
import org.example.domain.notification.domain.model.NotificationType;

import java.time.LocalDateTime;

@Getter
public class NotificationResponseDto {

    private Long id;
    private NotificationType type;
    private String message;
    private Long referenceId;
    private boolean isRead;
    private LocalDateTime createdAt;

    public NotificationResponseDto(Notification notification) {
        this.id = notification.getId();
        this.type = notification.getType();
        this.message = notification.getMessage();
        this.referenceId = notification.getReferenceId();
        this.isRead = notification.isRead();
        this.createdAt = notification.getCreatedAt();
    }

    public static NotificationResponseDto from(Notification notification) {
        return new NotificationResponseDto(notification);
    }
}
