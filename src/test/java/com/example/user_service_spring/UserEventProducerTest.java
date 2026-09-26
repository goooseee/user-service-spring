package com.example.user_service_spring;

import com.example.user_service_spring.DTO.UserEventDTO;
import com.example.user_service_spring.DTO.UserEventDTO.EventType;
import com.example.user_service_spring.kafka.UserEventProducer;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserEventProducerTest {

    @Mock
    private KafkaTemplate<String, UserEventDTO> kafkaTemplate;

    @InjectMocks
    private UserEventProducer userEventProducer;

    private static final String EXPECTED_TOPIC = "user-events";

    @Test
    @DisplayName("1. Успешная отправка: сообщение передается в KafkaTemplate и срабатывает логирование успеха")
    void shouldSendUserEventSuccessfully() {
        String email = "test@example.com";
        EventType eventType = EventType.CREATED;

        TopicPartition topicPartition = new TopicPartition(EXPECTED_TOPIC, 0);
        RecordMetadata metadata = new RecordMetadata(topicPartition, 0, 100, System.currentTimeMillis(), 0, 0);
        SendResult<String, UserEventDTO> sendResult = new SendResult<>(null, metadata);

        CompletableFuture<SendResult<String, UserEventDTO>> future = CompletableFuture.completedFuture(sendResult);
        when(kafkaTemplate.send(eq(EXPECTED_TOPIC), eq(email), any(UserEventDTO.class)))
                .thenReturn(future);

        userEventProducer.sendUserEvent(email, eventType);

        ArgumentCaptor<UserEventDTO> captor = ArgumentCaptor.forClass(UserEventDTO.class);
        verify(kafkaTemplate, times(1)).send(eq(EXPECTED_TOPIC), eq(email), captor.capture());

        UserEventDTO capturedEvent = captor.getValue();
        assertThat(capturedEvent).isNotNull();
        assertThat(capturedEvent.email()).isEqualTo(email);
        assertThat(capturedEvent.eventType()).isEqualTo(eventType);
    }

    @Test
    @DisplayName("2. Ошибка отправки: Kafka упала, срабатывает ветка ошибки (ex != null)")
    void shouldHandleKafkaErrorGracefully() {
        String email = "failed@example.com";
        EventType eventType = EventType.CREATED;

        CompletableFuture<SendResult<String, UserEventDTO>> failedFuture = 
                CompletableFuture.failedFuture(new RuntimeException("Kafka connection timeout"));

        when(kafkaTemplate.send(eq(EXPECTED_TOPIC), eq(email), any(UserEventDTO.class)))
                .thenReturn(failedFuture);

        userEventProducer.sendUserEvent(email, eventType);

        verify(kafkaTemplate, times(1)).send(eq(EXPECTED_TOPIC), eq(email), any(UserEventDTO.class));
    }
}