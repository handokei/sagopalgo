package org.example.domain.notification.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.global.config.entity.BaseEntity;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notification_user_id", columnList = "userId"),
        @Index(name = "idx_notification_is_read", columnList = "isRead")
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String message;

    private Long referenceId;

    private boolean isRead = false;

    private boolean isDeleted = false;

    @Enumerated(EnumType.STRING)
    private NotificationStatus notificationStatus = NotificationStatus.PENDING;

    private Notification(Long userId, NotificationType type, String message, Long referenceId) {
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.referenceId = referenceId;
        this.notificationStatus = NotificationStatus.PENDING;
    }

    public static Notification of(Long userId, NotificationType type, String message, Long referenceId) {
        return new Notification(userId, type, message, referenceId);
    }

    public void markAsRead() {
        this.isRead = true;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void markAsSent() {
        this.notificationStatus = NotificationStatus.SENT;
    }

    public void markAsFailed() {
        this.notificationStatus = NotificationStatus.FAILED;
    }
}
