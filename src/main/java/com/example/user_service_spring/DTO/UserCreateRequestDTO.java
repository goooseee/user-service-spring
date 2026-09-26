package com.example.user_service_spring.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
@Schema(description = "DTO для создания нового пользователя")
public record UserCreateRequestDTO(
		@Schema(description = "Имя пользователя", example = "Иван Иванов")
		@NotBlank(message = "Name cannot be empty")
		String name,
		@Schema(description = "Электронная почта", example = "ivan@example.com")
		@NotBlank @Email(message = "Invalid email format")
		String email,
		@Schema(description = "Возраст", example = "25")
		@Min(value = 0, message = "Age must be positive")
		int age) {}
