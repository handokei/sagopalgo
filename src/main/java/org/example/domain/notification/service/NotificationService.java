package org.example.domain.notification.service;

import org.example.domain.notification.domain.model.NotificationType;

public interface NotificationService {

    void send(Long userId, NotificationType type, String message, Long referenceId);
}
