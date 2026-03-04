package org.example.domain.notification.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.notification.event.OrderNotificationEvent;
import org.example.domain.notification.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "notification.order", groupId = "notification-group")
    public void consume(OrderNotificationEvent event) {
        log.info("Kafka consume - userId: {}, type: {}", event.getUserId(), event.getType());
        notificationService.send(event.getUserId(), event.getType(), event.getMessage(), event.getReferenceId());
    }
}
