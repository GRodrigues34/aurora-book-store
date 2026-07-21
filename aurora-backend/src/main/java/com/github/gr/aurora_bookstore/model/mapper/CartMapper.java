package com.github.gr.aurora_bookstore.model.mapper;

import com.github.gr.aurora_bookstore.dto.cartDto.CartItemDTO;
import com.github.gr.aurora_bookstore.dto.cartDto.CartReadDTO;
import com.github.gr.aurora_bookstore.model.entity.Cart;
import com.github.gr.aurora_bookstore.model.entity.CartItem;

import java.util.stream.Collectors;

public class CartMapper {

    public static CartReadDTO toCartReadDto(Cart cart) {
        CartReadDTO dto = new CartReadDTO();
        if (cart.getCartItems() != null) {
            dto.setCartItems(cart.getCartItems().stream().map(CartMapper::toItemDto).collect(Collectors.toSet()));
        }
        return dto;
    }

    public static CartItemDTO toItemDto(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setId(cartItem.getId());
        dto.setBook(BookMapper.toReadDto(cartItem.getBook()));
        dto.setQuantity(cartItem.getQuantity());
        return dto;
    }
}
