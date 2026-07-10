package com.github.gr.aurora_bookstore.dto.bookDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class BookCreateDto {

    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    @NotNull(message = "Price is required")
    private Float price;
    private String imageUrl;
    @NotNull(message = "Stock is required")
    private Integer stock;
    private Set<Long> authorIds;
    private Set<Long> categoryIds;
    private Set<Long> genreIds;

}
