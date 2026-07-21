package com.github.gr.aurora_bookstore.controller;

import com.github.gr.aurora_bookstore.dto.cartDto.CartItemInsertDTO;
import com.github.gr.aurora_bookstore.dto.cartDto.CartReadDTO;
import com.github.gr.aurora_bookstore.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartReadDTO> insertItem(@RequestBody CartItemInsertDTO itemDto, Principal principal){
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.insertItem(Long.valueOf(principal.getName()), itemDto));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartReadDTO> getCart(@RequestParam Long userId){
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @DeleteMapping("/{itemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartReadDTO> deleteItem(@PathVariable Long itemId){
        return ResponseEntity.ok(cartService.deleteItem(itemId));
    }
}
