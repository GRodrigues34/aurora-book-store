package com.github.gr.aurora_bookstore.model.mapper;

import com.github.gr.aurora_bookstore.dto.categoryDto.CategoryCreateDto;
import com.github.gr.aurora_bookstore.dto.categoryDto.CategoryReadDto;
import com.github.gr.aurora_bookstore.model.entity.Category;

public class CategoryMapper {

    public static CategoryReadDto toReadDto(Category category) {
        if (category == null) return null;
        CategoryReadDto dto = new CategoryReadDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    public static Category toEntity(CategoryCreateDto dto) {
        if (dto == null) return null;
        return Category.builder()
                .name(dto.getName())
                .build();
    }
}
