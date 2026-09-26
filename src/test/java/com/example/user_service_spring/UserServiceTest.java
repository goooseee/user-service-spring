package com.example.user_service_spring;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.user_service_spring.DTO.UserCreateRequestDTO;
import com.example.user_service_spring.DTO.UserEventDTO.EventType;
import com.example.user_service_spring.DTO.UserResponseDTO;
import com.example.user_service_spring.entity.User;
import com.example.user_service_spring.exception.UserNotFoundException;
import com.example.user_service_spring.kafka.UserEventProducer;
import com.example.user_service_spring.mapper.UserMapper;
import com.example.user_service_spring.repository.UserRepository;
import com.example.user_service_spring.service.UserService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
	
	@Mock
    private UserRepository userRepository;

    @Mock
    private UserEventProducer userEventProducer;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UserService userService;
	
    @Test
    @DisplayName("Успешное сохранение пользователя и отправка события в Kafka")
    void saveUser_Success() {
        UserCreateRequestDTO requestDto = new UserCreateRequestDTO("John", "john@example.com", 30);
        User savedUser = new User("John", "john@example.com", 30);
        UserResponseDTO expectedResponse = new UserResponseDTO(1L, "John", "john@example.com", 30, LocalDateTime.now());

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(mapper.userToDTO(savedUser)).thenReturn(expectedResponse);

        UserResponseDTO actualResponse = userService.saveUser(requestDto);

        assertThat(actualResponse).isEqualTo(expectedResponse);

        verify(userRepository, times(1)).save(any(User.class));
        verify(userEventProducer, times(1))
                .sendUserEvent("john@example.com", EventType.CREATED);
        verify(mapper, times(1)).userToDTO(savedUser);
    }
    
    @Test
    @DisplayName("Успешный поиск пользователя по ID")
    void getUserById_Success() {
        Long userId = 1L;
        User user = new User("John", "john@example.com", 30);
        UserResponseDTO expectedResponse = new UserResponseDTO(userId, "John", "john@example.com", 30, LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.userToDTO(user)).thenReturn(expectedResponse);

        UserResponseDTO actualResponse = userService.getUserById(userId);

        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Выброс исключения UserNotFoundException, если пользователь не найден")
    void getUserById_NotFound_ThrowsException() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found with id: " + userId);

        verify(mapper, never()).userToDTO(any());
    }
    
    @Test
    @DisplayName("Успешное удаление и отправка события DELETED")
    void deleteUserById_Success() {
        Long userId = 1L;
        User user = new User("John", "john@example.com", 30);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUserById(userId);

        verify(userRepository).deleteById(userId);
        verify(userEventProducer)
                .sendUserEvent("john@example.com", EventType.DELETED);
    }

    @Test
    @DisplayName("Ошибка удаления — пользователь не найден")
    void deleteUserById_NotFound_ThrowsException() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUserById(userId))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).deleteById(any());
        verify(userEventProducer, never()).sendUserEvent(any(), any());
    }
    
}
