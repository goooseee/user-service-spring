package com.example.user_service_spring.controller;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "API управления пользователями")
public class UserController {
	
	private final UserService userService;
	
	@GetMapping("/{id}")
	@Operation(summary = "Получить пользователя по ID", description = "Возвращает данные пользователя с HATEOAS-ссылками")
    @ApiResponse(responseCode = "200", description = "Пользователь найден")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
	public ResponseEntity<EntityModel<UserResponseDTO>> getUser(@PathVariable("id") Long id) {
		UserResponseDTO user = userService.getUserById(id);
        
        EntityModel<UserResponseDTO> resource = EntityModel.of(user,
                linkTo(methodOn(UserController.class).getUser(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).updateUser(id, null)).withRel("update"),
                linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete"),
                linkTo(methodOn(UserController.class).getAllUser()).withRel("all-users")
        );

        return ResponseEntity.ok(resource);
	}
	
	@GetMapping
    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей с навигационными ссылками")
    public ResponseEntity<CollectionModel<EntityModel<UserResponseDTO>>> getAllUser() {
        List<EntityModel<UserResponseDTO>> users = userService.getAllUsers().stream()
                .map(user -> EntityModel.of(user,
                		linkTo(methodOn(UserController.class).getUser(user.id())).withSelfRel(),
                        linkTo(methodOn(UserController.class).getAllUser()).withRel("all-users")
                ))
                .toList();

        CollectionModel<EntityModel<UserResponseDTO>> result = CollectionModel.of(users,
                linkTo(methodOn(UserController.class).getAllUser()).withSelfRel()
        );

        return ResponseEntity.ok(result);
    }
	
	@PostMapping
	@Operation(summary = "Создать пользователя", description = "Создает пользователя и отправляет событие в Kafka")
    @ApiResponse(responseCode = "201", description = "Пользователь успешно создан")
	public ResponseEntity<EntityModel<UserResponseDTO>> saveUser(@RequestBody @Valid UserCreateRequestDTO dto) {
        UserResponseDTO createdUser = userService.saveUser(dto);
        Long id = createdUser.id();

        EntityModel<UserResponseDTO> resource = EntityModel.of(createdUser,
                linkTo(methodOn(UserController.class).getUser(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUser()).withRel("all-users")
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }
	
	@PutMapping("/{id}")
	@Operation(summary = "Обновить пользователя по ID")
	public ResponseEntity<EntityModel<UserResponseDTO>> updateUser(
            @PathVariable("id") Long id,
            @RequestBody UserUpdateRequestDTO dto) {
        UserResponseDTO updatedUser = userService.updateUser(id, dto);

        EntityModel<UserResponseDTO> resource = EntityModel.of(updatedUser,
                linkTo(methodOn(UserController.class).getUser(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUser()).withRel("all-users")
        );

        return ResponseEntity.ok(resource);
    }
	
	@DeleteMapping("/{id}")
	@Operation(summary = "Удалить пользователя по ID")
    @ApiResponse(responseCode = "204", description = "Пользователь успешно удален")
	public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
