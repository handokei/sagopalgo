package org.example.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.domain.notification.event.OrderNotificationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public ObjectMapper kafkaObjectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Bean
    public KafkaTemplate<String, OrderNotificationEvent> kafkaTemplate(
            DefaultKafkaProducerFactory<String, OrderNotificationEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public DefaultKafkaProducerFactory<String, OrderNotificationEvent> producerFactory(
            ObjectMapper kafkaObjectMapper,
            org.springframework.boot.autoconfigure.kafka.KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildProducerProperties(null);
        return new DefaultKafkaProducerFactory<>(props, null, new JsonSerializer<>(kafkaObjectMapper));
    }

    @Bean
    public ConsumerFactory<String, OrderNotificationEvent> consumerFactory(
            ObjectMapper kafkaObjectMapper,
            org.springframework.boot.autoconfigure.kafka.KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);
        JsonDeserializer<OrderNotificationEvent> deserializer =
                new JsonDeserializer<>(OrderNotificationEvent.class, kafkaObjectMapper);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(props, null, deserializer);
    }
}
