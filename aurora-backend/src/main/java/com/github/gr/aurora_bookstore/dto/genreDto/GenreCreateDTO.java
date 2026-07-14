package com.github.gr.aurora_bookstore.dto.genreDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GenreCreateDTO {
    @NotBlank(message = "Genre name is required")
    private String name;
}
