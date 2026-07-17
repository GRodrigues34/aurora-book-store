package com.github.gr.aurora_bookstore.dto.orderDto;

import lombok.Data;

import java.util.Set;

@Data
public class OrderCreateDTO {
    private Set<OrderItemCreateDTO> orderItems;
}
