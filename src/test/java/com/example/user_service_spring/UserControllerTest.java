package com.example.user_service_spring;

import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.user_service_spring.DTO.UserCreateRequestDTO;
import com.example.user_service_spring.DTO.UserResponseDTO;
import com.example.user_service_spring.controller.UserController;
import com.example.user_service_spring.exception.UserNotFoundException;
import com.example.user_service_spring.service.UserService;

import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
	
	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;
    
    @Test
    @DisplayName("GET /api/v1/users/{id} — Успешное получение пользователя с HATEOAS ссылками")
    void getUser_Success() throws Exception {
        Long userId = 1L;
        UserResponseDTO userDto = new UserResponseDTO(userId, "John", "john@example.com", 30, LocalDateTime.now());

        when(userService.getUserById(userId)).thenReturn(userDto);

        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/api/v1/users/1"))
                .andExpect(jsonPath("$._links.update.href").value("http://localhost/api/v1/users/1"))
                .andExpect(jsonPath("$._links.delete.href").value("http://localhost/api/v1/users/1"))
                .andExpect(jsonPath("$._links.all-users.href").value("http://localhost/api/v1/users"));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} — Возвращает 404, если пользователь не найден")
    void getUser_NotFound() throws Exception {
        Long userId = 999L;
        when(userService.getUserById(userId)).thenThrow(new UserNotFoundException("User not found with id: " + userId));

        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/users — Создание пользователя (201 Created)")
    void saveUser_Success() throws Exception {
        UserCreateRequestDTO requestDto = new UserCreateRequestDTO("Jane", "jane@example.com", 25);
        UserResponseDTO responseDto = new UserResponseDTO(2L, "Jane", "jane@example.com", 25, LocalDateTime.now());

        when(userService.saveUser(any(UserCreateRequestDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Jane"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} — Удаление пользователя (204 No Content)")
    void deleteUser_Success() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/api/v1/users/{id}", userId))
                .andExpect(status().isNoContent());
    }
	
}
