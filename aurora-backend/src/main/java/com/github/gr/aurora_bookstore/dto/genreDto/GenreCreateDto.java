package com.github.gr.aurora_bookstore.dto.genreDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GenreCreateDto {
    @NotBlank(message = "Genre name is required")
    private String name;
}
