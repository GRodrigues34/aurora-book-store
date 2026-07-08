package com.github.gr.aurora_bookstore.model.mapper;

import com.github.gr.aurora_bookstore.dto.genreDto.GenreCreateDto;
import com.github.gr.aurora_bookstore.dto.genreDto.GenreReadDto;
import com.github.gr.aurora_bookstore.model.entity.Genre;

public class GenreMapper {

    public static GenreReadDto toReadDto(Genre genre) {
        if (genre == null) return null;
        GenreReadDto dto = new GenreReadDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }

    public static Genre toEntity(GenreCreateDto dto) {
        if (dto == null) return null;
        return Genre.builder()
                .name(dto.getName())
                .build();
    }
}
