package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.cartDto.CartItemInsertDTO;
import com.github.gr.aurora_bookstore.dto.cartDto.CartReadDTO;
import com.github.gr.aurora_bookstore.model.entity.Book;
import com.github.gr.aurora_bookstore.model.entity.Cart;
import com.github.gr.aurora_bookstore.model.entity.CartItem;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.repository.BookRepository;
import com.github.gr.aurora_bookstore.repository.CartRepository;
import com.github.gr.aurora_bookstore.repository.UserRepository;
import com.github.gr.aurora_bookstore.util.BookTestData;
import com.github.gr.aurora_bookstore.util.UserTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Captor
    private ArgumentCaptor<Cart> cartCaptor;

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = UserTestData.createValidUserUser();
        book = BookTestData.createValidBook();
    }

    @Test
    void shouldCreateCartSuccessfully() {
        // Arrange
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Act
        cartService.create(user.getId());

        // Assert
        verify(userRepository, times(1)).findById(user.getId());
        verify(cartRepository, times(1)).save(cartCaptor.capture());

        Cart savedCart = cartCaptor.getValue();
        assertEquals(user, savedCart.getUser());
        assertTrue(savedCart.getCartItems() == null || savedCart.getCartItems().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenCreatingCartForNonExistentUser() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> cartService.create(999L));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void shouldAddItemToCartSuccessfully() {
        // Arrange
        CartItemInsertDTO cartInsertDto = new CartItemInsertDTO(book.getId(), 2);

        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);
        cart.setCartItems(new HashSet<>());

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        // Act
        CartReadDTO result = cartService.insert(user.getId(), cartInsertDto);

        // Assert
        verify(cartRepository, times(1)).save(cartCaptor.capture());
        Cart savedCart = cartCaptor.getValue();

        assertEquals(1, savedCart.getCartItems().size());
        CartItem addedItem = savedCart.getCartItems().iterator().next();
        assertEquals(book.getId(), addedItem.getBook().getId());
        assertEquals(2, addedItem.getQuantity());
        assertEquals(cart, addedItem.getCart());
    }

    @Test
    void shouldThrowExceptionWhenInsertingItemAndBookDoesNotExist() {
        // Arrange
        CartItemInsertDTO cartInsertDto = new CartItemInsertDTO(999L, 2);
        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> cartService.insert(user.getId(), cartInsertDto));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void shouldRemoveItemFromCartSuccessfully() {
        // Arrange
        Long itemIdToDelete = 100L;

        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);
        cart.setCartItems(new HashSet<>());

        CartItem item = new CartItem();
        item.setId(itemIdToDelete);
        item.setCart(cart);
        item.setBook(book);
        item.setQuantity(1);

        cart.getCartItems().add(item);

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

        // Act
        CartReadDTO result = cartService.itemDelete(user.getId(), itemIdToDelete);

        // Assert
        verify(cartRepository, times(1)).save(cartCaptor.capture());
        Cart savedCart = cartCaptor.getValue();

        assertTrue(savedCart.getCartItems().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenRemovingNonExistentItem() {
        // Arrange
        Long itemIdToDelete = 999L;

        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);
        cart.setCartItems(new HashSet<>());

        CartItem item = new CartItem();
        item.setId(100L); // Different ID
        item.setCart(cart);
        cart.getCartItems().add(item);

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> cartService.itemDelete(user.getId(), itemIdToDelete));
        verify(cartRepository, never()).save(any(Cart.class));
    }
}
