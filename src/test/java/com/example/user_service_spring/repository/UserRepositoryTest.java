package com.example.user_service_spring.repository;

import com.example.user_service_spring.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Сохранение пользователя и автоматическое заполнение createdAt")
    void saveUser_ShouldPersistUserAndSetCreatedAt() {
        User user = new User("Иван", "ivan@mail.com", 25);

        User savedUser = userRepository.save(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Иван");
        assertThat(savedUser.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Поиск пользователя по ID")
    void findById_ShouldReturnUserWhenExists() {
        User user = userRepository.save(new User("Анна", "anna@mail.com", 30));

        Optional<User> foundUser = userRepository.findById(user.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("anna@mail.com");
    }

    @Test
    @DisplayName("Удаление пользователя из БД")
    void deleteUser_ShouldRemoveUserFromDatabase() {
        User user = userRepository.save(new User("Сергей", "sergey@mail.com", 40));
        Long userId = user.getId();

        userRepository.deleteById(userId);

        Optional<User> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isEmpty();
    }
}