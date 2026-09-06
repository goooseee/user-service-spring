package com.example.user_service_spring.DTO;


public record UserEventDTO(
    String email,
    EventType eventType
) {
    public enum EventType {
        CREATED,
        DELETED
    }
}
