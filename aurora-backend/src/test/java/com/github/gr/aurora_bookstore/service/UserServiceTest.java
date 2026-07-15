package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.userDto.UserReadDTO;
import com.github.gr.aurora_bookstore.dto.userDto.UserRegisterDTO;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.model.enums.UserRole;
import com.github.gr.aurora_bookstore.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // Arrange
        User user = new User();
        user.setEmail("test@email.com");
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));

        // Act
        UserDetails result = userService.loadUserByUsername("test@email.com");

        // Assert
        assertNotNull(result);
        assertEquals("test@email.com", ((User) result).getEmail());
    }

    @Test
    void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowUsernameNotFoundException() {
        // Arrange
        when(userRepository.findByEmail("notfound@email.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("notfound@email.com");
        });
    }

    @Test
    void create_ShouldSaveUserAndReturnDto() {
        // Arrange
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@test.com");
        dto.setPassword("pass");
        dto.setRole(UserRole.USER);

        User savedEntity = new User();
        savedEntity.setId(1L);
        savedEntity.setUsername("testuser");
        savedEntity.setEmail("test@test.com");
        savedEntity.setRole(UserRole.USER);

        when(userRepository.save(any(User.class))).thenReturn(savedEntity);

        // Act
        UserReadDTO result = userService.create(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@test.com", result.getEmail());
    }

    @Test
    void findById_WhenUserExists_ShouldReturnDto() {
        // Arrange
        User user = new User();
        user.setId(2L);
        user.setUsername("user2");
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        // Act
        UserReadDTO result = userService.findById(2L);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("user2", result.getUsername());
    }

    @Test
    void findById_WhenUserDoesNotExist_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.findById(99L);
        });

        assertEquals("User not found with id: 99", exception.getMessage());
    }

    @Test
    void delete_WhenUserExists_ShouldDeleteUser() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // Act
        assertDoesNotThrow(() -> userService.delete(1L));
        
        // Assert
        verify(userRepository, times(1)).deleteById(1L);
    }
}
