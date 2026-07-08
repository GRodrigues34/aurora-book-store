package com.github.gr.aurora_bookstore.controller;

import com.github.gr.aurora_bookstore.dto.genreDto.GenreCreateDto;
import com.github.gr.aurora_bookstore.dto.genreDto.GenreReadDto;
import com.github.gr.aurora_bookstore.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<List<GenreReadDto>> findAll() {
        return ResponseEntity.ok(genreService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreReadDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(genreService.findById(id));
    }

    @PostMapping
    public ResponseEntity<GenreReadDto> create(@Valid @RequestBody GenreCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(genreService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreReadDto> update(@PathVariable Long id, @Valid @RequestBody GenreCreateDto dto) {
        return ResponseEntity.ok(genreService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        genreService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
