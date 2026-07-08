package com.github.gr.aurora_bookstore.dto.bookDto;

import com.github.gr.aurora_bookstore.dto.authorDto.AuthorReadDto;
import com.github.gr.aurora_bookstore.dto.categoryDto.CategoryReadDto;
import com.github.gr.aurora_bookstore.dto.genreDto.GenreReadDto;
import lombok.Data;

import java.util.Set;

@Data
public class BookReadDto {

    private Long id;
    private String title;
    private String description;
    private Float price;
    private String imageUrl;
    private Set<AuthorReadDto> authors;
    private Set<CategoryReadDto> categories;
    private Set<GenreReadDto> genres;
}
