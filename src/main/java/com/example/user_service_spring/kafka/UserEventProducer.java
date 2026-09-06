package com.example.user_service_spring.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.user_service_spring.DTO.UserEventDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventProducer {

    private final KafkaTemplate<String, UserEventDTO> kafkaTemplate;
    private static final String TOPIC = "user-events";

    public void sendUserEvent(String email, UserEventDTO.EventType eventType) {
        UserEventDTO event = new UserEventDTO(email, eventType);

        kafkaTemplate.send(TOPIC, email, event).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Успешно отправлено событие [{}] для email: {} в топик: {} [partition: {}, offset: {}]", 
                        eventType, email, TOPIC, 
                        result.getRecordMetadata().partition(), 
                        result.getRecordMetadata().offset());
            } else {
                log.error("Ошибка при отправке события [{}] для email: {} в Kafka", eventType, email, ex);
            }
        });
    }
}