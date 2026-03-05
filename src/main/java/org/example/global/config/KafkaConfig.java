package org.example.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
public class KafkaConfig {

    // 토픽 자동 생성
    @Bean
    public NewTopic notificationOrderTopic() {
        return TopicBuilder.name("notification.order").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic notificationOrderDltTopic() {
        return TopicBuilder.name("notification.order-dlt").partitions(3).replicas(1).build();
    }

    // ObjectMapper
    @Bean
    public ObjectMapper kafkaObjectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // 제네릭 KafkaTemplate - 모든 이벤트 타입 사용 가능
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ObjectMapper kafkaObjectMapper,
                                                        KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildProducerProperties(null);
        JsonSerializer<Object> serializer = new JsonSerializer<>(kafkaObjectMapper);
        serializer.setAddTypeInfo(true);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props, null, serializer));
    }
}
