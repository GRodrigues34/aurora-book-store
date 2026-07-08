package com.github.gr.aurora_bookstore.dto.authorDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthorCreateDto {
    @NotBlank(message = "Author name is required")
    private String name;
}
