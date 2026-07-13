package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.userDto.UserCreateDto;
import com.github.gr.aurora_bookstore.dto.userDto.UserReadDto;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.model.mapper.UserMapper;
import com.github.gr.aurora_bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserReadDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toReadDto)
                .collect(Collectors.toList());
    }

    public UserReadDto findById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toReadDto)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User getEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public UserReadDto create(UserCreateDto dto) {
        User user = UserMapper.toEntity(dto);
        User savedUser = userRepository.save(user);
        return UserMapper.toReadDto(savedUser);
    }

    public UserReadDto update(Long id, UserCreateDto dto) {
        User existingUser = getEntityById(id);
        existingUser.setUsername(dto.getUsername());
        existingUser.setEmail(dto.getEmail());
        existingUser.setPassword(dto.getPassword());
        existingUser.setRole(dto.getRole());
        User updatedUser = userRepository.save(existingUser);
        return UserMapper.toReadDto(updatedUser);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
