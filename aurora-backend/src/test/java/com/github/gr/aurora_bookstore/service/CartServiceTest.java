package com.github.gr.aurora_bookstore.service;

import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.model.enums.UserRole;
import com.github.gr.aurora_bookstore.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {
    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private BookRepository bookRepository;

    @Test
    void create_ShouldReturnCartReadDto(){
        //Arrange
        CartCreateDTO cartDto= new CartCreateDTO();
        cartDto.setUserId(1L);

        User user = new User();
        user.setId(1L);
        user.setUsername("cartUser");
        user.setEmail("cart@email.com");
        user.setPassword("password");
        user.setRole(UserRole.valueOf("USER"));


        //Act
        CartReadDTO result = cartService.create(cartDto);

        //Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getUserId());
        assertEquals(0, result.getBooks().size());
    }

    @Test 
    void insert_ShouldReturnCartReadDto(){
        //Arrange
        CartInsertDTO cartDto= new CartInsertDto();
        cartDto.setBookId(1L);
        cartDto.setUserId(1L);
        cartDto.setQuantity(1l);


        //Act
        CartReadDTO result = cartService.insert(cartDto);

        //Assert

    }
}
