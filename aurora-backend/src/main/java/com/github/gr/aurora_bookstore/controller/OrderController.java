package com.github.gr.aurora_bookstore.controller;

import com.github.gr.aurora_bookstore.dto.orderDto.OrderCreateDTO;
import com.github.gr.aurora_bookstore.dto.orderDto.OrderReadDTO;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final com.github.gr.aurora_bookstore.service.CheckoutService checkoutService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderReadDTO> createOrder(@RequestBody OrderCreateDTO dto, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(user.getId(), dto));
    }

    @PostMapping("/checkout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderReadDTO> checkout(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(checkoutService.checkout(user.getId()));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderReadDTO>> getOrders(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(user.getId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
