package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.categoryDto.CategoryCreateDTO;
import com.github.gr.aurora_bookstore.dto.categoryDto.CategoryReadDTO;
import com.github.gr.aurora_bookstore.exception.bookException.CategoryNotFoundException;
import com.github.gr.aurora_bookstore.model.entity.Category;
import com.github.gr.aurora_bookstore.model.mapper.CategoryMapper;
import com.github.gr.aurora_bookstore.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryReadDTO> findAll() {
        return categoryRepository.findAll().stream()
                .map(CategoryMapper::toReadDto)
                .collect(Collectors.toList());
    }

    public CategoryReadDTO findById(Long id) {
        return categoryRepository.findById(id)
                .map(CategoryMapper::toReadDto)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
    }

    public CategoryReadDTO create(CategoryCreateDTO dto) {
        Category category = CategoryMapper.toEntity(dto);
        Category savedCategory = categoryRepository.save(category);
        return CategoryMapper.toReadDto(savedCategory);
    }

    public CategoryReadDTO update(Long id, CategoryCreateDTO dto) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        existingCategory.setName(dto.getName());
        Category updatedCategory = categoryRepository.save(existingCategory);
        return CategoryMapper.toReadDto(updatedCategory);
    }

    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
