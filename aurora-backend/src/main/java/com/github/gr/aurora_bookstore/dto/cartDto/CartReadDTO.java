package com.github.gr.aurora_bookstore.dto.cartDto;

import com.github.gr.aurora_bookstore.model.entity.CartItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class CartReadDTO {
    private Set<CartItemDTO> cartItems;
}
