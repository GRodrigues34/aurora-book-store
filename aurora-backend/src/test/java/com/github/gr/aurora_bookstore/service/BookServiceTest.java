package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.bookDto.BookCreateDTO;
import com.github.gr.aurora_bookstore.dto.bookDto.BookReadDTO;
import com.github.gr.aurora_bookstore.exception.bookException.BookNotFoundException;
import com.github.gr.aurora_bookstore.model.entity.Author;
import com.github.gr.aurora_bookstore.model.entity.Book;
import com.github.gr.aurora_bookstore.model.entity.Category;
import com.github.gr.aurora_bookstore.util.BookTestData;
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
        Book mockBook = BookTestData.createValidBook();
        
        BookCreateDTO dto = new BookCreateDTO();
        dto.setTitle(mockBook.getTitle());
        dto.setPrice(mockBook.getPrice());
        dto.setStock(mockBook.getStock());
        dto.setAuthorIds(Set.of(1L));
        dto.setCategoryIds(Set.of(1L));

        Author mockAuthor = BookTestData.createValidAuthor();
        Category mockCategory = BookTestData.createValidCategory();

        when(authorRepository.findAllById(any())).thenReturn(List.of(mockAuthor));
        when(categoryRepository.findAllById(any())).thenReturn(List.of(mockCategory));
        when(bookRepository.save(any(Book.class))).thenReturn(mockBook);

        // Act
        BookReadDTO result = bookService.create(dto);

        // Assert
        assertNotNull(result);
        assertEquals(mockBook.getId(), result.getId());
        assertEquals(mockBook.getTitle(), result.getTitle());

        // Assert related mocks were called
        verify(authorRepository, times(1)).findAllById(any());
        verify(categoryRepository, times(1)).findAllById(any());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void findById_WhenBookExists_ShouldReturnDto() {
        // Arrange
        Book mockBook = BookTestData.createValidBook();
        when(bookRepository.findById(mockBook.getId())).thenReturn(Optional.of(mockBook));

        // Act
        BookReadDTO result = bookService.findById(mockBook.getId());

        // Assert
        assertNotNull(result);
        assertEquals(mockBook.getId(), result.getId());
        assertEquals(mockBook.getTitle(), result.getTitle());
    }

    @Test
    void findById_WhenBookDoesNotExist_ShouldThrowException() {
        // Arrange
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> {
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
