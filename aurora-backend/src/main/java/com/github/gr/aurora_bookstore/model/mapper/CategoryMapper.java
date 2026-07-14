package com.github.gr.aurora_bookstore.model.mapper;

import com.github.gr.aurora_bookstore.dto.categoryDto.CategoryCreateDTO;
import com.github.gr.aurora_bookstore.dto.categoryDto.CategoryReadDTO;
import com.github.gr.aurora_bookstore.model.entity.Category;

public class CategoryMapper {

    public static CategoryReadDTO toReadDto(Category category) {
        if (category == null) return null;
        CategoryReadDTO dto = new CategoryReadDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    public static Category toEntity(CategoryCreateDTO dto) {
        if (dto == null) return null;
        return Category.builder()
                .name(dto.getName())
                .build();
    }
}
