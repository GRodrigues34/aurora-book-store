package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.bookDto.BookCreateDTO;
import com.github.gr.aurora_bookstore.dto.bookDto.BookReadDTO;
import com.github.gr.aurora_bookstore.model.entity.Author;
import com.github.gr.aurora_bookstore.model.entity.Book;
import com.github.gr.aurora_bookstore.model.entity.Category;
import com.github.gr.aurora_bookstore.repository.AuthorRepository;
import com.github.gr.aurora_bookstore.repository.BookRepository;
import com.github.gr.aurora_bookstore.repository.CategoryRepository;
import com.github.gr.aurora_bookstore.repository.GenreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private GenreRepository genreRepository;

    @Test
    void create_ShouldSaveBookAndReturnDto() {
        // Arrange
        BookCreateDTO dto = new BookCreateDTO();
        dto.setTitle("O Guia do Mochileiro das Galáxias");
        dto.setPrice(42.0f);
        dto.setStock(10);
        dto.setAuthorIds(Set.of(1L));
        dto.setCategoryIds(Set.of(2L));

        Author author = new Author();
        author.setId(1L);
        author.setName("Douglas Adams");

        Category category = new Category();
        category.setId(2L);
        category.setName("Ficção Científica");

        when(authorRepository.findAllById(Set.of(1L))).thenReturn(List.of(author));
        when(categoryRepository.findAllById(Set.of(2L))).thenReturn(List.of(category));

        Book savedBook = new Book();
        savedBook.setId(100L);
        savedBook.setTitle("O Guia do Mochileiro das Galáxias");
        savedBook.setPrice(42.0f);
        savedBook.setAuthors(Set.of(author));
        savedBook.setCategories(Set.of(category));

        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        // Act
        BookReadDTO result = bookService.create(dto);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("O Guia do Mochileiro das Galáxias", result.getTitle());

        // Assert related mocks were called
        verify(authorRepository, times(1)).findAllById(Set.of(1L));
        verify(categoryRepository, times(1)).findAllById(Set.of(2L));
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void findById_WhenBookExists_ShouldReturnDto() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setTitle("1984");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // Act
        BookReadDTO result = bookService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("1984", result.getTitle());
    }

    @Test
    void findById_WhenBookDoesNotExist_ShouldThrowException() {
        // Arrange
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookService.findById(99L);
        });

        assertEquals("Book not found with id: 99", exception.getMessage());
    }

    @Test
    void delete_WhenBookExists_ShouldDeleteBook() {
        // Arrange
        when(bookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1L);

        // Act
        assertDoesNotThrow(() -> bookService.delete(1L));

        // Assert
        verify(bookRepository, times(1)).deleteById(1L);
    }
}
