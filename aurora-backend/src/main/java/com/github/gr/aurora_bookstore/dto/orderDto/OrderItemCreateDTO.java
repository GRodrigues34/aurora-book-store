package com.github.gr.aurora_bookstore.dto.orderDto;

import lombok.Data;

@Data
public class OrderItemCreateDTO {
    private Long bookId;
    private Integer quantity;
}
