package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.bookDto.BookCreateDTO;
import com.github.gr.aurora_bookstore.dto.orderDto.OrderReadDTO;
import com.github.gr.aurora_bookstore.model.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckoutServiceTest {

    @InjectMocks
    private CheckoutService checkoutService;

    @Mock
    private CartService cartService;

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(10L);
        user.setUsername("testuser");
        user.setEmail("user@test.com");

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setPrice(new BigDecimal("10.00"));
        book.setStock(5);
        book.setAuthors(new HashSet<>());
        book.setCategories(new HashSet<>());
        book.setGenres(new HashSet<>());
    }

    @Test
    void shouldPerformCheckoutSuccessfully() {
        // Arrange
        Long userId = 10L;
        Cart cart = new Cart();
        cart.setId(100L);
        cart.setUser(user);

        CartItem cartItem = new CartItem();
        cartItem.setId(2L);
        cartItem.setBook(book);
        cartItem.setQuantity(2);
        cartItem.setCart(cart);
        cart.setCartItems(Set.of(cartItem));

        OrderItem orderItem = new OrderItem();
        orderItem.setBook(book);
        orderItem.setQuantity(2);
        orderItem.setTotalPrice(new BigDecimal("20.00"));

        when(cartService.getEntityByUserId(userId)).thenReturn(cart);
        when(userService.getEntityById(userId)).thenReturn(user);
        when(orderService.createProcessedOrderItem(book.getId(), 2)).thenReturn(orderItem);
        when(orderService.save(any(Orders.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        OrderReadDTO result = checkoutService.checkout(userId);

        // Assert
        assertNotNull(result);
        verify(cartService, times(1)).getEntityByUserId(userId);
        verify(orderService, times(1)).createProcessedOrderItem(book.getId(), 2);
        verify(orderService, times(1)).save(any(Orders.class));
        verify(cartService, times(1)).clearCart(userId);
    }
}
