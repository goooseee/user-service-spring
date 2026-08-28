package com.example.user_service_spring.service;


import com.example.user_service_spring.DTO.UserCreateRequestDTO;
import com.example.user_service_spring.DTO.UserResponseDTO;
import com.example.user_service_spring.DTO.UserUpdateRequestDTO;
import com.example.user_service_spring.entity.User;
import com.example.user_service_spring.exception.UserNotFoundException;
import com.example.user_service_spring.mapper.UserMapper;
import com.example.user_service_spring.repository.UserRepository;
import com.example.user_service_spring.service.UserService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("getUserById - Успешный поиск пользователя по ID")
    void getUserById_Success() {
        Long userId = 1L;
        User user = new User("Иван", "ivan@mail.com", 25);
        UserResponseDTO expectedDto = new UserResponseDTO(userId, "Иван", "ivan@mail.com", 25, LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.userToDTO(user)).thenReturn(expectedDto);

        UserResponseDTO actualDto = userService.getUserById(userId);

        assertThat(actualDto).isNotNull();
        assertThat(actualDto.id()).isEqualTo(userId);
        assertThat(actualDto.email()).isEqualTo("ivan@mail.com");

        verify(userRepository, times(1)).findById(userId);
        verify(mapper, times(1)).userToDTO(user);
    }

    @Test
    @DisplayName("getUserById - Выброс UserNotFoundException, если пользователь не найден")
    void getUserById_NotFound_ThrowsException() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found with id: " + userId);

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("saveUser - Успешное сохранение пользователя")
    void saveUser_Success() {
        UserCreateRequestDTO requestDto = new UserCreateRequestDTO("Алексей", "alex@mail.com", 30);
        User savedUser = new User("Алексей", "alex@mail.com", 30);
        UserResponseDTO expectedDto = new UserResponseDTO(1L, "Алексей", "alex@mail.com", 30, LocalDateTime.now());

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(mapper.userToDTO(savedUser)).thenReturn(expectedDto);

        UserResponseDTO actualDto = userService.saveUser(requestDto);

        assertThat(actualDto).isNotNull();
        assertThat(actualDto.name()).isEqualTo("Алексей");

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("deleteUserById - Успешное удаление существующего пользователя")
    void deleteUserById_Success() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUserById(userId);

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("deleteUserById - Выброс исключения при попытке удалить несуществующего пользователя")
    void deleteUserById_NotFound_ThrowsException() {
        Long userId = 99L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUserById(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Cannot delete");

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("updateUser - Успешное обновление полей пользователя")
    void updateUser_Success() {
        Long userId = 1L;
        UserUpdateRequestDTO updateDto = new UserUpdateRequestDTO("Новое Имя", "new@mail.com", 26);
        User existingUser = new User("Старое Имя", "old@mail.com", 25);
        User updatedUser = new User("Новое Имя", "new@mail.com", 26);
        UserResponseDTO expectedDto = new UserResponseDTO(userId, "Новое Имя", "new@mail.com", 26, LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(mapper.userToDTO(updatedUser)).thenReturn(expectedDto);

        UserResponseDTO actualDto = userService.updateUser(userId, updateDto);

        assertThat(actualDto.name()).isEqualTo("Новое Имя");
        assertThat(actualDto.email()).isEqualTo("new@mail.com");

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
    }
}