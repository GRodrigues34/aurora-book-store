package com.github.gr.aurora_bookstore.model.mapper;

import com.github.gr.aurora_bookstore.dto.authorDto.AuthorCreateDTO;
import com.github.gr.aurora_bookstore.dto.authorDto.AuthorReadDTO;
import com.github.gr.aurora_bookstore.model.entity.Author;

public class AuthorMapper {

    public static AuthorReadDTO toReadDto(Author author) {
        if (author == null) return null;
        AuthorReadDTO dto = new AuthorReadDTO();
        dto.setId(author.getId());
        dto.setName(author.getName());
        return dto;
    }

    public static Author toEntity(AuthorCreateDTO dto) {
        if (dto == null) return null;
        return Author.builder()
                .name(dto.getName())
                .build();
    }
}
