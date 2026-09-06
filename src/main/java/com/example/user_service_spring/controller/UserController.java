package com.example.user_service_spring.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_service_spring.DTO.UserResponseDTO;
import com.example.user_service_spring.DTO.UserCreateRequestDTO;
import com.example.user_service_spring.DTO.UserUpdateRequestDTO;
import com.example.user_service_spring.entity.User;
import com.example.user_service_spring.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDTO> getUser(@PathVariable("id") Long id) {
		return ResponseEntity.ok( userService.getUserById( id ) );
	}
	
	@GetMapping
	public ResponseEntity<List<UserResponseDTO>> getAllUser(){
		return ResponseEntity.ok(userService.getAllUsers());
	}
	
	@PostMapping
	public ResponseEntity<UserResponseDTO> saveUser(@RequestBody @Valid UserCreateRequestDTO dto) {
		UserResponseDTO createdUser = userService.saveUser(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<UserResponseDTO> updateUser(
			@PathVariable("id") Long id,
			@RequestBody @Valid UserUpdateRequestDTO dto) {
		return ResponseEntity.ok( userService.updateUser( id, dto ) );
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
		userService.deleteUserById( id );
		return ResponseEntity.noContent().build();
	}
}
