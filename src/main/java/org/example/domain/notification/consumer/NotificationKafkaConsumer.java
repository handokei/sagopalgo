package org.example.domain.notification.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.notification.domain.model.NotificationType;
import org.example.global.constants.KafkaTopics;
import org.example.domain.notification.event.OrderNotificationEvent;
import org.example.domain.notification.event.ProductDiscountNotificationEvent;
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
    @KafkaListener(topics = KafkaTopics.ORDER_NOTIFICATION, groupId = "notification-group")
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

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(topics = KafkaTopics.PRODUCT_DISCOUNT, groupId = "notification-group")
    public void consumeDiscount(ProductDiscountNotificationEvent event) {
        log.info("Kafka consume discount - productId: {}, userCount: {}",
                event.getProductId(), event.getUserIds().size());
        event.getUserIds().forEach(userId ->
                notificationService.send(userId, NotificationType.PRODUCT_DISCOUNT, event.getMessage(), event.getProductId())
        );
        log.info("Kafka consume discount success - productId: {}", event.getProductId());
    }

    @DltHandler
    public void handleDiscountDlt(ProductDiscountNotificationEvent event) {
        log.error("Kafka DLT - 할인 알림 최종 실패, productId: {}, message: {}",
                event.getProductId(), event.getMessage());
    }
}
