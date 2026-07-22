package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.bookDto.BookCreateDTO;
import com.github.gr.aurora_bookstore.dto.bookDto.BookReadDTO;
import com.github.gr.aurora_bookstore.model.entity.*;
import com.github.gr.aurora_bookstore.model.mapper.BookMapper;
import com.github.gr.aurora_bookstore.repository.AuthorRepository;
import com.github.gr.aurora_bookstore.repository.BookRepository;
import com.github.gr.aurora_bookstore.repository.CategoryRepository;
import com.github.gr.aurora_bookstore.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final GenreRepository genreRepository;

    public List<BookReadDTO> findAll() {
        return bookRepository.findAll().stream()
                .map(BookMapper::toReadDto)
                .collect(Collectors.toList());
    }

    public BookReadDTO findById(Long id) {
        return bookRepository.findById(id)
                .map(BookMapper::toReadDto)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    public Book getEntityById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public BookReadDTO create(BookCreateDTO dto) {
        Set<Author> authors = resolveAuthors(dto.getAuthorIds());
        Set<Category> categories = resolveCategories(dto.getCategoryIds());
        Set<Genre> genres = resolveGenres(dto.getGenreIds());

        Book book = BookMapper.toEntity(dto, authors, categories, genres);
        Book savedBook = bookRepository.save(book);
        return BookMapper.toReadDto(savedBook);
    }

    public BookReadDTO update(Long id, BookCreateDTO dto) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        existingBook.setTitle(dto.getTitle());
        existingBook.setDescription(dto.getDescription());
        existingBook.setPrice(dto.getPrice());
        existingBook.setImageUrl(dto.getImageUrl());
        existingBook.setStock(dto.getStock());

        existingBook.setAuthors(resolveAuthors(dto.getAuthorIds()));
        existingBook.setCategories(resolveCategories(dto.getCategoryIds()));
        existingBook.setGenres(resolveGenres(dto.getGenreIds()));

        Book updatedBook = bookRepository.save(existingBook);
        return BookMapper.toReadDto(updatedBook);
    }

    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    private Set<Author> resolveAuthors(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();
        return new HashSet<>(authorRepository.findAllById(ids));
    }

    private Set<Category> resolveCategories(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();
        return new HashSet<>(categoryRepository.findAllById(ids));
    }

    private Set<Genre> resolveGenres(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return new HashSet<>();
        return new HashSet<>(genreRepository.findAllById(ids));
    }
}
