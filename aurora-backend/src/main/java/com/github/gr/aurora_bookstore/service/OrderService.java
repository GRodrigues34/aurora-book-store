package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.orderDto.OrderCreateDTO;
import com.github.gr.aurora_bookstore.dto.orderDto.OrderReadDTO;
import com.github.gr.aurora_bookstore.model.entity.Book;
import com.github.gr.aurora_bookstore.model.entity.OrderItem;
import com.github.gr.aurora_bookstore.model.entity.Orders;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.model.mapper.OrderMapper;
import com.github.gr.aurora_bookstore.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final BookService bookService;

    @Transactional
    public OrderReadDTO createOrder(Long userId, OrderCreateDTO orderCreateDTO) {
        User user = userService.getEntityById(userId);
        Orders order = OrderMapper.toEntity(orderCreateDTO);
        order.setUser(user);

        BigDecimal totalPrice = BigDecimal.ZERO;
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                Book book = bookService.getEntityById(item.getBook().getId());
                if (book.getStock() < item.getQuantity()) {
                    throw new RuntimeException("Insufficient stock for book: " + book.getTitle());
                }
                
                // Reduce stock
                book.setStock(book.getStock() - item.getQuantity());

                BigDecimal itemPrice = book.getPrice();
                BigDecimal itemTotal = itemPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

                item.setItemPrice(itemPrice);
                item.setTotalPrice(itemTotal);
                item.setBook(book);

                totalPrice = totalPrice.add(itemTotal);
            }
        }
        order.setTotalPrice(totalPrice);
        Orders savedOrder = orderRepository.save(order);
        return OrderMapper.toReadDto(savedOrder);
    }

    public List<OrderReadDTO> getOrdersByUserId(Long userId) {
        List<Orders> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(OrderMapper::toReadDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        orderRepository.delete(order);
    }
}
