package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.orderDto.OrderCreateDTO;
import com.github.gr.aurora_bookstore.dto.orderDto.OrderItemCreateDTO;
import com.github.gr.aurora_bookstore.dto.orderDto.OrderReadDTO;
import com.github.gr.aurora_bookstore.model.entity.Book;
import com.github.gr.aurora_bookstore.model.entity.Orders;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.repository.OrderRepository;
import com.github.gr.aurora_bookstore.exception.orderException.InsufficientStockException;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookService bookService;

    @Captor
    private ArgumentCaptor<Orders> orderCaptor;

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = UserTestData.createValidUserUser();
        book = BookTestData.createValidBook();
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        // Arrange
        OrderItemCreateDTO itemDto = new OrderItemCreateDTO();
        itemDto.setBookId(book.getId());
        itemDto.setQuantity(1);

        OrderCreateDTO orderCreateDto = new OrderCreateDTO();
        orderCreateDto.setOrderItems(Set.of(itemDto));

        // Adjust book stock to 10 for success
        book.setStock(10);

        when(userService.getEntityById(user.getId())).thenReturn(user);
        when(bookService.getEntityById(book.getId())).thenReturn(book);
        when(orderRepository.save(any(Orders.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        OrderReadDTO result = orderService.createOrder(user.getId(), orderCreateDto);

        // Assert
        verify(orderRepository, times(1)).save(orderCaptor.capture());
        Orders savedOrder = orderCaptor.getValue();

        assertNotNull(result);
        assertEquals(user, savedOrder.getUser());
        assertEquals(new BigDecimal("1.0"), savedOrder.getTotalPrice());
        assertEquals(9, book.getStock()); // stock reduced from 10 to 9
        assertEquals(1, savedOrder.getOrderItems().size());
    }

    @Test
    void shouldThrowExceptionWhenBookStockIsInsufficient() {
        // Arrange
        OrderItemCreateDTO itemDto = new OrderItemCreateDTO();
        itemDto.setBookId(book.getId());
        itemDto.setQuantity(5); // request 5

        OrderCreateDTO orderCreateDto = new OrderCreateDTO();
        orderCreateDto.setOrderItems(Set.of(itemDto));

        book.setStock(2); // only has 2

        when(userService.getEntityById(user.getId())).thenReturn(user);
        when(bookService.getEntityById(book.getId())).thenReturn(book);

        // Act & Assert
        assertThrows(InsufficientStockException.class, () -> orderService.createOrder(user.getId(), orderCreateDto));
        verify(orderRepository, never()).save(any(Orders.class));
    }

    @Test
    void shouldGetOrdersByUserIdSuccessfully() {
        // Arrange
        Orders order = new Orders();
        order.setId(10L);
        order.setUser(user);
        order.setTotalPrice(BigDecimal.TEN);

        when(orderRepository.findByUserId(user.getId())).thenReturn(List.of(order));

        // Act
        List<OrderReadDTO> result = orderService.getOrdersByUserId(user.getId());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getOrderId());
        assertEquals(BigDecimal.TEN, result.get(0).getTotalPrice());
    }

    @Test
    void shouldDeleteOrderSuccessfully() {
        // Arrange
        Long orderId = 100L;
        Orders order = new Orders();
        order.setId(orderId);
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        orderService.deleteOrder(orderId);

        // Assert
        verify(orderRepository, times(1)).delete(order);
    }
}
