package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.userDto.UserReadDTO;
import com.github.gr.aurora_bookstore.dto.userDto.UserRegisterDTO;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.model.enums.UserRole;
import com.github.gr.aurora_bookstore.util.UserTestData;
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
        User user = UserTestData.createValidUserUser();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act
        UserDetails result = userService.loadUserByUsername(user.getEmail());

        // Assert
        assertNotNull(result);
        assertEquals(user.getEmail(), ((User) result).getEmail());
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
        User mockEntity = UserTestData.createValidUserUser();
        dto.setUsername(mockEntity.getUsername());
        dto.setEmail(mockEntity.getEmail());
        dto.setPassword(mockEntity.getPassword());
        dto.setRole(mockEntity.getRole());

        when(userRepository.save(any(User.class))).thenReturn(mockEntity);

        // Act
        UserReadDTO result = userService.create(dto);

        // Assert
        assertNotNull(result);
        assertEquals(mockEntity.getId(), result.getId());
        assertEquals(mockEntity.getUsername(), result.getUsername());
        assertEquals(mockEntity.getEmail(), result.getEmail());
    }

    @Test
    void findById_WhenUserExists_ShouldReturnDto() {
        // Arrange
        User user = UserTestData.createValidAdminUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Act
        UserReadDTO result = userService.findById(user.getId());

        // Assert
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getUsername(), result.getUsername());
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
