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

    private static final String TOPIC = "notification.order";

    private final KafkaTemplate<String, OrderNotificationEvent> kafkaTemplate;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderNotification(OrderNotificationEvent event) {
        log.info("Kafka produce - topic: {}, userId: {}, type: {}", TOPIC, event.getUserId(), event.getType());
        kafkaTemplate.send(TOPIC, event.getUserId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Kafka produce failed - userId: {}, type: {}, error: {}",
                                event.getUserId(), event.getType(), ex.getMessage());
                    } else {
                        log.info("Kafka produce success - offset: {}", result.getRecordMetadata().offset());
                    }
                });
    }
}
