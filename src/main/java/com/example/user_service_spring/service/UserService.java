package com.example.user_service_spring.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.user_service_spring.DTO.UserResponseDTO;
import com.example.user_service_spring.DTO.UserCreateRequestDTO;
import com.example.user_service_spring.DTO.UserEventDTO;
import com.example.user_service_spring.DTO.UserUpdateRequestDTO;
import com.example.user_service_spring.entity.User;
import com.example.user_service_spring.exception.UserNotFoundException;
import com.example.user_service_spring.kafka.UserEventProducer;
import com.example.user_service_spring.mapper.UserMapper;
import com.example.user_service_spring.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	
	private final UserRepository userRepository;
	private final UserEventProducer userEventProducer;
	
	private final UserMapper mapper;
	@Transactional
	public UserResponseDTO saveUser(UserCreateRequestDTO dto) {
		User user = new User(dto.name(), dto.email(), dto.age());
		User savedUser = userRepository.save( user );
		userEventProducer.sendUserEvent(savedUser.getEmail(), UserEventDTO.EventType.CREATED);
		return mapper.userToDTO( savedUser );
	}
	
	public UserResponseDTO getUserById(Long id) {
		return userRepository.findById( id )
				.map(mapper::userToDTO
				 ).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
	}
	
	public boolean isUserPresent(Long id) {
		return userRepository.existsById( id );
	}
	
	public List<UserResponseDTO> getAllUsers(){
		return userRepository.findAll().stream()
			.map(mapper::userToDTO).toList();
	}
	@Transactional
	public void deleteUserById(Long id) {
		User user = userRepository.findById(id)
	            .orElseThrow(() -> new UserNotFoundException("Cannot delete. User not found with id: " + id));
		userRepository.deleteById( id );
		userEventProducer.sendUserEvent(user.getEmail(), UserEventDTO.EventType.DELETED);
	}
	@Transactional
	public UserResponseDTO updateUser(Long id,UserUpdateRequestDTO dto) {
		User user = userRepository.findById( id )
				.orElseThrow(() -> new UserNotFoundException("Cannot update. User not found with id: " + id));
		user.setName(dto.name());
        user.setEmail(dto.email());
        user.setAge(dto.age());
        
        User userUpdated = userRepository.save( user );
		return mapper.userToDTO( userUpdated );
	}
	
}
