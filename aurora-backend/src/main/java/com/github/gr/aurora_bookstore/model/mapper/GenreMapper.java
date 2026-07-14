package com.github.gr.aurora_bookstore.model.mapper;

import com.github.gr.aurora_bookstore.dto.genreDto.GenreCreateDTO;
import com.github.gr.aurora_bookstore.dto.genreDto.GenreReadDTO;
import com.github.gr.aurora_bookstore.model.entity.Genre;

public class GenreMapper {

    public static GenreReadDTO toReadDto(Genre genre) {
        if (genre == null) return null;
        GenreReadDTO dto = new GenreReadDTO();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }

    public static Genre toEntity(GenreCreateDTO dto) {
        if (dto == null) return null;
        return Genre.builder()
                .name(dto.getName())
                .build();
    }
}
