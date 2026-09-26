package com.example.user_service_spring.DTO;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(description = "DTO ответа с полной информацией о пользователе")
public record UserResponseDTO(
		@Schema(description = "Уникальный идентификатор пользователя", example = "1")
		Long id,
		@Schema(description = "Имя пользователя", example = "Иван Иванов")
		String name,
		@Schema(description = "Электронная почта", example = "ivan@example.com")
		String email,
		@Schema(description = "Возраст пользователя", example = "25")
		int age,
		@Schema(description = "Дата и время создания записи", example = "2026-03-29T14:30:00")
		LocalDateTime createdAt) {
}
