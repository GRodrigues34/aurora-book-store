package com.github.gr.aurora_bookstore.model.mapper;

import com.github.gr.aurora_bookstore.dto.userDto.UserCreateDto;
import com.github.gr.aurora_bookstore.dto.userDto.UserReadDto;
import com.github.gr.aurora_bookstore.model.entity.User;

public class UserMapper {

    public static UserReadDto toReadDto(User user) {
        if (user == null) return null;
        UserReadDto dto = new UserReadDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }

    public static User toEntity(UserCreateDto dto) {
        if (dto == null) return null;
        return User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .role(dto.getRole())
                .build();
    }
}
