package com.github.gr.aurora_bookstore.controller;

import com.github.gr.aurora_bookstore.dto.cartDto.CartItemInsertDTO;
import com.github.gr.aurora_bookstore.dto.cartDto.CartReadDTO;
import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartReadDTO> insertItem(@RequestBody CartItemInsertDTO itemDto, @AuthenticationPrincipal User user){
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.insertItem(user.getId(), itemDto));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartReadDTO> getCart(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(cartService.getCart(user.getId()));
    }

    @DeleteMapping("/{itemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartReadDTO> deleteItem(@PathVariable Long itemId, @AuthenticationPrincipal User user){
        return ResponseEntity.ok(cartService.deleteItem(user.getId(), itemId));
    }
}
