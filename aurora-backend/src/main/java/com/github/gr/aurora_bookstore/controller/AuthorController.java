package com.github.gr.aurora_bookstore.controller;

import com.github.gr.aurora_bookstore.dto.authorDto.AuthorCreateDto;
import com.github.gr.aurora_bookstore.dto.authorDto.AuthorReadDto;
import com.github.gr.aurora_bookstore.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<List<AuthorReadDto>> findAll() {
        return ResponseEntity.ok(authorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorReadDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AuthorReadDto> create(@Valid @RequestBody AuthorCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorReadDto> update(@PathVariable Long id, @Valid @RequestBody AuthorCreateDto dto) {
        return ResponseEntity.ok(authorService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
