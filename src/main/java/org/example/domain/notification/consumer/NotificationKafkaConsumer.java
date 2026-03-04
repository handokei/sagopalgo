package org.example.domain.notification.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.notification.event.OrderNotificationEvent;
import org.example.domain.notification.service.NotificationService;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaConsumer {

    private final NotificationService notificationService;

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(topics = "notification.order", groupId = "notification-group")
    public void consume(OrderNotificationEvent event) {
        log.info("Kafka consume - userId: {}, type: {}", event.getUserId(), event.getType());
        notificationService.send(event.getUserId(), event.getType(), event.getMessage(), event.getReferenceId());
        log.info("Kafka consume success - userId: {}, type: {}", event.getUserId(), event.getType());
    }

    @DltHandler
    public void handleDlt(OrderNotificationEvent event) {
        log.error("Kafka DLT - 최종 실패, userId: {}, type: {}, message: {}",
                event.getUserId(), event.getType(), event.getMessage());
    }
}
