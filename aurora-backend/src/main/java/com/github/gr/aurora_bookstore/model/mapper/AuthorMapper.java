package com.github.gr.aurora_bookstore.model.mapper;

import com.github.gr.aurora_bookstore.dto.authorDto.AuthorCreateDto;
import com.github.gr.aurora_bookstore.dto.authorDto.AuthorReadDto;
import com.github.gr.aurora_bookstore.model.entity.Author;

public class AuthorMapper {

    public static AuthorReadDto toReadDto(Author author) {
        if (author == null) return null;
        AuthorReadDto dto = new AuthorReadDto();
        dto.setId(author.getId());
        dto.setName(author.getName());
        return dto;
    }

    public static Author toEntity(AuthorCreateDto dto) {
        if (dto == null) return null;
        return Author.builder()
                .name(dto.getName())
                .build();
    }
}
