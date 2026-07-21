package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.cartDto.CartItemInsertDTO;
import com.github.gr.aurora_bookstore.dto.cartDto.CartReadDTO;
import com.github.gr.aurora_bookstore.model.entity.Book;
import com.github.gr.aurora_bookstore.model.entity.Cart;
import com.github.gr.aurora_bookstore.model.entity.CartItem;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.repository.CartItemRepository;
import com.github.gr.aurora_bookstore.repository.CartRepository;
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
    private CartItemRepository cartItemRepository;

    @Mock
    private BookService bookService;

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
        // Act
        cartService.create(user);

        // Assert
        verify(cartRepository, times(1)).save(cartCaptor.capture());

        Cart savedCart = cartCaptor.getValue();
        assertEquals(user, savedCart.getUser());
    }

    @Test
    void shouldAddItemToCartSuccessfully() {
        // Arrange
        CartItemInsertDTO cartInsertDto = new CartItemInsertDTO(book.getId(), 2);

        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);
        cart.setCartItems(new HashSet<>());

        when(cartRepository.findByUserId(user.getId())).thenReturn(cart);
        when(bookService.getEntityById(book.getId())).thenReturn(book);

        // Act
        CartReadDTO result = cartService.insertItem(user.getId(), cartInsertDto);

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
    void shouldThrowExceptionWhenInsertingItemAndCartDoesNotExist() {
        // Arrange
        CartItemInsertDTO cartInsertDto = new CartItemInsertDTO(book.getId(), 2);
        when(cartRepository.findByUserId(user.getId())).thenReturn(null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> cartService.insertItem(user.getId(), cartInsertDto));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void shouldThrowExceptionWhenInsertingItemAndBookDoesNotExist() {
        // Arrange
        CartItemInsertDTO cartInsertDto = new CartItemInsertDTO(999L, 2);
        Cart cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);

        when(cartRepository.findByUserId(user.getId())).thenReturn(cart);
        when(bookService.getEntityById(999L)).thenThrow(new RuntimeException("Book not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> cartService.insertItem(user.getId(), cartInsertDto));
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

        when(cartItemRepository.findById(itemIdToDelete)).thenReturn(Optional.of(item));

        // Act
        CartReadDTO result = cartService.deleteItem(user.getId(), itemIdToDelete);

        // Assert
        verify(cartRepository, times(1)).save(cartCaptor.capture());
        Cart savedCart = cartCaptor.getValue();

        assertTrue(savedCart.getCartItems().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenRemovingItemAndCartDoesNotExist() {
        // Arrange
        Long itemIdToDelete = 100L;
        CartItem item = new CartItem();
        item.setId(itemIdToDelete);
        item.setCart(null);

        when(cartItemRepository.findById(itemIdToDelete)).thenReturn(Optional.of(item));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> cartService.deleteItem(user.getId(), itemIdToDelete));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void shouldThrowExceptionWhenRemovingNonExistentItem() {
        // Arrange
        Long itemIdToDelete = 999L;

        when(cartItemRepository.findById(itemIdToDelete)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> cartService.deleteItem(user.getId(), itemIdToDelete));
        verify(cartRepository, never()).save(any(Cart.class));
    }
}
