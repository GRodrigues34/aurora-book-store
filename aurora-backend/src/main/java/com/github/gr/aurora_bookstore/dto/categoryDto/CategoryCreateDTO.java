package com.github.gr.aurora_bookstore.dto.categoryDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryCreateDTO {
    @NotBlank(message = "Category name is required")
    private String name;
}
