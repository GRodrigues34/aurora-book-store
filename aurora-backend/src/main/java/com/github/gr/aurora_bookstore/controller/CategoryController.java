package com.github.gr.aurora_bookstore.controller;

import com.github.gr.aurora_bookstore.dto.categoryDto.CategoryCreateDto;
import com.github.gr.aurora_bookstore.dto.categoryDto.CategoryReadDto;
import com.github.gr.aurora_bookstore.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryReadDto>> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryReadDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryReadDto> create(@Valid @RequestBody CategoryCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryReadDto> update(@PathVariable Long id, @Valid @RequestBody CategoryCreateDto dto) {
        return ResponseEntity.ok(categoryService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
