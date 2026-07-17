package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.cartDto.CartReadDTO;
import com.github.gr.aurora_bookstore.model.entity.Book;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.model.enums.UserRole;
import com.github.gr.aurora_bookstore.repository.BookRepository;
import com.github.gr.aurora_bookstore.repository.CartRepository;
import com.github.gr.aurora_bookstore.repository.UserRepository;
import com.github.gr.aurora_bookstore.util.BookTestData;
import com.github.gr.aurora_bookstore.util.UserTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {
    @InjectMocks
    private CartService cartService;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = UserTestData.createValidUserUser();
        book = BookTestData.createValidBook();
    }

    @Test
    void createCart() {

    }

    @Test
    void testAddToCart() {
    }

}
