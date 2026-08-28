package com.example.user_service_spring.controller;

import com.example.user_service_spring.DTO.UserCreateRequestDTO;
import com.example.user_service_spring.DTO.UserResponseDTO;
import com.example.user_service_spring.DTO.UserUpdateRequestDTO;
import com.example.user_service_spring.exception.UserNotFoundException;
import com.example.user_service_spring.service.UserService;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("GET /api/v1/users/{id} - Успешное получение пользователя")
    void getUserById_Success() throws Exception {
        UserResponseDTO responseDto = new UserResponseDTO(1L, "Иван", "ivan@mail.com", 25, LocalDateTime.now());
        when(userService.getUserById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Иван"))
                .andExpect(jsonPath("$.email").value("ivan@mail.com"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    @DisplayName("GET /api/v1/users - Получение всех пользователей")
    void getAllUsers_Success() throws Exception {
        UserResponseDTO user1 = new UserResponseDTO(1L, "Иван", "ivan@mail.com", 25, LocalDateTime.now());
        UserResponseDTO user2 = new UserResponseDTO(2L, "Анна", "anna@mail.com", 30, LocalDateTime.now());
        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Иван"))
                .andExpect(jsonPath("$[1].name").value("Анна"));
    }

    @Test
    @DisplayName("POST /api/v1/users - Успешное создание (201 Created)")
    void createUser_Success() throws Exception {
        UserCreateRequestDTO request = new UserCreateRequestDTO("Иван", "ivan@mail.com", 25);
        UserResponseDTO response = new UserResponseDTO(1L, "Иван", "ivan@mail.com", 25, LocalDateTime.now());

        when(userService.saveUser(any(UserCreateRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Иван"));
    }

    @Test
    @DisplayName("POST /api/v1/users - Ошибка валидации 400 Bad Request при невалидном email")
    void createUser_InvalidEmail_Returns400() throws Exception {
        UserCreateRequestDTO request = new UserCreateRequestDTO("Иван", "not-an-email", 25);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/users/{id} - Успешное обновление")
    void updateUser_Success() throws Exception {
        UserUpdateRequestDTO request = new UserUpdateRequestDTO("Иван Обновленный", "new_email@mail.com", 26);
        UserResponseDTO response = new UserResponseDTO(1L, "Иван Обновленный", "new_email@mail.com", 26, LocalDateTime.now());

        when(userService.updateUser(eq(1L), any(UserUpdateRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Иван Обновленный"))
                .andExpect(jsonPath("$.email").value("new_email@mail.com"));
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} - Успешное удаление (204 No Content)")
    void deleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUserById(1L);

        mockMvc.perform(delete("/api/v1/users/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
