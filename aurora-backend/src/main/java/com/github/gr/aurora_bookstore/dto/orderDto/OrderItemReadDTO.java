package com.github.gr.aurora_bookstore.dto.orderDto;

import java.math.BigDecimal;

import com.github.gr.aurora_bookstore.dto.bookDto.BookReadDTO;
import lombok.Data;

@Data
public class OrderItemReadDTO {
    private BookReadDTO book;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
}
