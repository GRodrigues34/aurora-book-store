package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.dto.cartDto.CartItemInsertDTO;
import com.github.gr.aurora_bookstore.dto.cartDto.CartReadDTO;
import com.github.gr.aurora_bookstore.exception.cartException.CartItemNotFoundException;
import com.github.gr.aurora_bookstore.exception.cartException.CartOwnershipException;
import com.github.gr.aurora_bookstore.exception.bookException.ResourceNotFoundException;
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
            throw new ResourceNotFoundException("Cart not found for user id: " + userId);
        }

        Book book = bookService.getEntityById(itemDto.bookId());

        // Check if the item already exists in the cart
        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(itemDto.bookId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + itemDto.quantity());
        } else {
            CartItem item = new CartItem();
            item.setBook(book);
            item.setQuantity(itemDto.quantity());
            item.setCart(cart);
            cart.getCartItems().add(item);
        }

        cartRepository.save(cart);
        return CartMapper.toCartReadDto(cart);
    }

    public CartReadDTO getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart not found for user id: " + userId);
        }
        return CartMapper.toCartReadDto(cart);
    }

    public CartReadDTO deleteItem(Long userId, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found with id: " + itemId));

        Cart cart = item.getCart();
        if (cart == null) {
            throw new ResourceNotFoundException("Cart not found");
        }

        if (!cart.getUser().getId().equals(userId)) {
            throw new CartOwnershipException("Access denied: This cart item does not belong to you.");
        }

        cart.getCartItems().remove(item);
        cartRepository.save(cart);
        return CartMapper.toCartReadDto(cart);
    }

    public Cart getEntityByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart not found for user id: " + userId);
        }
        return cart;
    }

    public void clearCart(Long userId) {
        Cart cart = getEntityByUserId(userId);
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }
}
