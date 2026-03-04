package org.example.domain.notification.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private static final String TOPIC = "notification.order";

    private final KafkaTemplate<String, OrderNotificationEvent> kafkaTemplate;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderNotification(OrderNotificationEvent event) {
        kafkaTemplate.send(TOPIC, event.getUserId().toString(), event);
    }
}
