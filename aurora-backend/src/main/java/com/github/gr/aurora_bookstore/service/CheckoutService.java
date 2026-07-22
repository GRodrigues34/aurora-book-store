package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.orderDto.OrderReadDTO;
import com.github.gr.aurora_bookstore.model.entity.*;
import com.github.gr.aurora_bookstore.model.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;

    @Transactional
    public OrderReadDTO checkout(Long userId) {
        Cart cart = cartService.getEntityByUserId(userId);
        OrderReadDTO orderReadDTO = createOrderFromCart(userId, cart);
        cartService.clearCart(userId);
        return orderReadDTO;
    }

    @Transactional
    public OrderReadDTO createOrderFromCart(Long userId, Cart cart) {
        User user = userService.getEntityById(userId);
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cannot create order from an empty cart");
        }

        Orders order = new Orders();
        order.setUser(user);
        order.setDate(java.time.LocalDateTime.now());

        BigDecimal totalPrice = BigDecimal.ZERO;
        Set<OrderItem> orderItems = new HashSet<>();

        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = orderService.createProcessedOrderItem(cartItem.getBook().getId(), cartItem.getQuantity());
            orderItem.setOrder(order);

            orderItems.add(orderItem);
            totalPrice = totalPrice.add(orderItem.getTotalPrice());
        }

        order.setOrderItems(orderItems);
        order.setTotalPrice(totalPrice);

        Orders savedOrder = orderService.save(order);
        return OrderMapper.toReadDto(savedOrder);

    }
}
