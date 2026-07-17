package com.github.gr.aurora_bookstore.dto.orderDto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class OrderReadDTO {
    private Long orderId;
    private Set<OrderItemReadDTO> orderItems;
    private BigDecimal totalPrice;
}
