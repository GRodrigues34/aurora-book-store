package com.github.gr.aurora_bookstore.dto.userDto;

import lombok.Data;

@Data
public class UserReadDto {
    private Long id;
    private String username;
    private String email;
    private String role;
}
