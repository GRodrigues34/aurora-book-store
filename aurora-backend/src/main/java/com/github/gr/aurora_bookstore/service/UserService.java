package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.userDto.UserRegisterDTO;
import com.github.gr.aurora_bookstore.dto.userDto.UserReadDTO;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.model.mapper.UserMapper;
import com.github.gr.aurora_bookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public List<UserReadDTO> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toReadDto)
                .collect(Collectors.toList());
    }

    public UserReadDTO findById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toReadDto)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public Boolean existsByEmail(String email){
        return userRepository.findByEmail(email) != null;
    }

    public UserDetails userDetailsFindByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public User getEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public UserReadDTO create(UserRegisterDTO dto) {
        User user = UserMapper.toEntity(dto);
        User savedUser = userRepository.save(user);
        return UserMapper.toReadDto(savedUser);
    }


    public UserReadDTO update(Long id, UserRegisterDTO dto) {
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
