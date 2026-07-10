package com.github.gr.aurora_bookstore.model.mapper;

import com.github.gr.aurora_bookstore.dto.bookDto.BookCreateDto;
import com.github.gr.aurora_bookstore.dto.bookDto.BookReadDto;
import com.github.gr.aurora_bookstore.model.entity.Author;
import com.github.gr.aurora_bookstore.model.entity.Book;
import com.github.gr.aurora_bookstore.model.entity.Category;
import com.github.gr.aurora_bookstore.model.entity.Genre;

import java.util.Set;
import java.util.stream.Collectors;

public class BookMapper {

    public static BookReadDto toReadDto(Book book) {
        if (book == null) return null;
        BookReadDto dto = new BookReadDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setDescription(book.getDescription());
        dto.setPrice(book.getPrice());
        dto.setImageUrl(book.getImageUrl());
        dto.setStock(book.getStock());

        if (book.getAuthors() != null) {
            dto.setAuthors(book.getAuthors().stream()
                    .map(AuthorMapper::toReadDto)
                    .collect(Collectors.toSet()));
        }
        
        if (book.getCategories() != null) {
            dto.setCategories(book.getCategories().stream()
                    .map(CategoryMapper::toReadDto)
                    .collect(Collectors.toSet()));
        }
        
        if (book.getGenres() != null) {
            dto.setGenres(book.getGenres().stream()
                    .map(GenreMapper::toReadDto)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    public static Book toEntity(BookCreateDto dto, Set<Author> authors, Set<Category> categories, Set<Genre> genres) {
        if (dto == null) return null;
        return Book.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .imageUrl(dto.getImageUrl())
                .stock(dto.getStock())
                .authors(authors)
                .categories(categories)
                .genres(genres)
                .build();
    }
}
