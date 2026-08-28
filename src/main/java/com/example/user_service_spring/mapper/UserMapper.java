package com.example.user_service_spring.mapper;

import org.springframework.stereotype.Component;

import com.example.user_service_spring.DTO.UserResponseDTO;
import com.example.user_service_spring.entity.User;
@Component
public class UserMapper {
	
	public UserResponseDTO userToDTO(User user) {
		return new UserResponseDTO(
				user.getId(),
				user.getName(),
				user.getEmail(),
				user.getAge(),
				user.getCreatedAt()
		);
	}

}
