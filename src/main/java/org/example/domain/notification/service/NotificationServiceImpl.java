package org.example.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.notification.controller.dto.NotificationResponseDto;
import org.example.domain.notification.domain.model.Notification;
import org.example.domain.notification.domain.model.NotificationType;
import org.example.domain.notification.domain.repository.NotificationRepository;
import org.example.domain.notification.exception.NotificationErrorCode;
import org.example.domain.notification.exception.NotificationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void send(Long userId, NotificationType type, String message, Long referenceId) {
        Notification notification = Notification.of(userId, type, message, referenceId);
        notificationRepository.save(notification);

        try {
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/notifications",
                    NotificationResponseDto.from(notification)
            );
            notification.markAsSent();
            log.info("알림 전송 성공 - userId: {}, type: {}, notificationId: {}", userId, type, notification.getId());
        } catch (Exception e) {
            notification.markAsFailed();
            log.error("알림 전송 실패 - userId: {}, type: {}, notificationId: {}, error: {}",
                    userId, type, notification.getId(), e.getMessage());
        }
    }

    public Page<NotificationResponseDto> getNotifications(Long userId, int page, int size) {
        Page<Notification> notifications = notificationRepository
                .findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
        return notifications.map(NotificationResponseDto::from);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalseAndIsDeletedFalse(userId);
    }

    @Transactional
    public NotificationResponseDto markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository
                .findByIdAndUserIdAndIsDeletedFalse(notificationId, userId)
                .orElseThrow(() -> new NotificationException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));

        notification.markAsRead();
        return NotificationResponseDto.from(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }
}
