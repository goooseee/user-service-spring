package com.example.user_service_spring.DTO;

import java.time.LocalDateTime;

public record UserResponseDTO(Long id,
	    String name,
	    String email,
	    int age,
	    LocalDateTime createdAt) {
}
