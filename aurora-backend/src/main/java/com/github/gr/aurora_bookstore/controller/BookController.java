package com.github.gr.aurora_bookstore.controller;

import com.github.gr.aurora_bookstore.dto.bookDto.BookCreateDto;
import com.github.gr.aurora_bookstore.dto.bookDto.BookReadDto;
import com.github.gr.aurora_bookstore.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookReadDto>> findAll() {
        return ResponseEntity.ok(bookService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookReadDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.findById(id));
    }

    @PostMapping
    public ResponseEntity<BookReadDto> create(@Valid @RequestBody BookCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookReadDto> update(@PathVariable Long id, @Valid @RequestBody BookCreateDto dto) {
        return ResponseEntity.ok(bookService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
