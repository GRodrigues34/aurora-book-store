package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.cartDto.CartItemDeleteDTO;
import com.github.gr.aurora_bookstore.dto.cartDto.CartItemInsertDTO;
import com.github.gr.aurora_bookstore.dto.cartDto.CartReadDTO;
import com.github.gr.aurora_bookstore.model.entity.Book;
import com.github.gr.aurora_bookstore.model.entity.Cart;
import com.github.gr.aurora_bookstore.model.entity.CartItem;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.model.mapper.CartMapper;
import com.github.gr.aurora_bookstore.repository.CartItemRepository;
import com.github.gr.aurora_bookstore.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookService bookService;

    public void create(User user) {
        Cart newCart = new Cart();
        newCart.setUser(user);
        cartRepository.save(newCart);
    }

    public CartReadDTO insertItem(Long userId, CartItemInsertDTO itemDto) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            throw new RuntimeException("Cart not found");
        }

        Book book = bookService.getEntityById(itemDto.bookId());

        CartItem item = new CartItem();
        item.setBook(book);
        item.setQuantity(itemDto.quantity());
        item.setCart(cart);

        cart.getCartItems().add(item);
        cartRepository.save(cart);
        return CartMapper.toCartReadDto(cart);
    }

    public CartReadDTO getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        return CartMapper.toCartReadDto(cart);
    }

    public CartReadDTO deleteItem(Long userId, CartItemDeleteDTO itemDeleteDTO) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            throw new RuntimeException("Cart not found");
        }

        CartItem item = cartItemRepository.findById(itemDeleteDTO.itemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        cart.getCartItems().remove(item);
        cartRepository.save(cart);
        return CartMapper.toCartReadDto(cart);
    }
}
