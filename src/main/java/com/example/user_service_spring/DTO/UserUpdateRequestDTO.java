package com.example.user_service_spring.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequestDTO(
		@NotBlank(message = "Name cannot be empty")
		String name,
		@NotBlank @Email(message = "Invalid email format")
		String email,
		@Min(value = 0, message = "Age must be positive")
		int age) {}
