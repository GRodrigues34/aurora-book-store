package com.github.gr.aurora_bookstore.dto.cartDto;

import com.github.gr.aurora_bookstore.dto.bookDto.BookReadDTO;
import lombok.Data;

@Data
public class CartItemDTO {
    private Long id;
    private BookReadDTO book;
    private Integer quantity;
}
