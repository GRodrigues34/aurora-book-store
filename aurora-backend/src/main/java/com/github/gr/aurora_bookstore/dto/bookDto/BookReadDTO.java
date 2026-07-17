package com.github.gr.aurora_bookstore.dto.bookDto;

import com.github.gr.aurora_bookstore.dto.authorDto.AuthorReadDTO;
import com.github.gr.aurora_bookstore.dto.categoryDto.CategoryReadDTO;
import com.github.gr.aurora_bookstore.dto.genreDto.GenreReadDTO;
import lombok.Data;

import java.math.BigDecimal;

import java.util.Set;

@Data
public class BookReadDTO {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Integer stock;
    private Set<AuthorReadDTO> authors;
    private Set<CategoryReadDTO> categories;
    private Set<GenreReadDTO> genres;
}
