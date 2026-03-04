package org.example.domain.notification.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private static final String ORDER_TOPIC = "notification.order";
    private static final String DISCOUNT_TOPIC = "notification.product.discount";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderNotification(OrderNotificationEvent event) {
        log.info("Kafka produce - topic: {}, userId: {}, type: {}", ORDER_TOPIC, event.getUserId(), event.getType());
        kafkaTemplate.send(ORDER_TOPIC, event.getUserId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Kafka produce failed - userId: {}, type: {}, error: {}",
                                event.getUserId(), event.getType(), ex.getMessage());
                    } else {
                        log.info("Kafka produce success - offset: {}", result.getRecordMetadata().offset());
                    }
                });
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductDiscountNotification(ProductDiscountNotificationEvent event) {
        log.info("Kafka produce - topic: {}, productId: {}, userCount: {}",
                DISCOUNT_TOPIC, event.getProductId(), event.getUserIds().size());
        kafkaTemplate.send(DISCOUNT_TOPIC, event.getProductId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Kafka produce failed - productId: {}, error: {}",
                                event.getProductId(), ex.getMessage());
                    } else {
                        log.info("Kafka produce success - offset: {}", result.getRecordMetadata().offset());
                    }
                });
    }
}
