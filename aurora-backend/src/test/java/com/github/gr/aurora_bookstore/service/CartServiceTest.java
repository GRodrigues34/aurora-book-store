package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.cartDto.CartItemInsertDTO;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.github.gr.aurora_bookstore.model.entity.Cart;

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
    void testcreate_shouldExist() {
        // Arrange
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Act
        cartService.create(user.getId());

        // Assert
        verify(userRepository, times(1)).findById(user.getId());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testInsert_shouldInsert() {
        // Arrange
        CartItemInsertDTO cartInsertDto = new CartItemInsertDTO(book.getId(), 2);
        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        Cart cartSaved = new Cart();
        cartSaved.setId(10L);
        cartSaved.setUser(user);

        // Simular o comportamento do save do Cart
        when(cartRepository.save(any(Cart.class))).thenReturn(cartSaved);

        // Act
        CartReadDTO result = cartService.insert(user.getId(), cartInsertDto);

        // Assert
        verify(cartRepository, times(1)).findByUserId(user.getId());
        verify(bookRepository, times(1)).findById(book.getId());
        verify(cartRepository, times(1)).save(any(Cart.class));
        assertNotNull(result);
    }

    @Test
    void testDeleteItem() {
        // Arrange
        Long itemIdToDelete = 100L;
        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Act
        CartReadDTO result = cartService.itemDelete(user.getId(), itemIdToDelete);

        // Assert
        verify(cartRepository, times(1)).save(any(Cart.class));
        assertNotNull(result);
    }

}
