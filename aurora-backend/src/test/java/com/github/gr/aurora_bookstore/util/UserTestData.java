package com.github.gr.aurora_bookstore.util;


import com.github.gr.aurora_bookstore.model.entity.User;
import com.github.gr.aurora_bookstore.model.enums.UserRole;

public class UserTestData {

    public static User createValidUserUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("test@email.com");
        user.setPassword("password");
        user.setRole(UserRole.USER);
        return user;
    }

    public static User createValidAdminUser(){
        User user = new User();
        user.setId(2L);
        user.setUsername("adminUser");
        user.setEmail("admin@email.com");
        user.setPassword("password");
        user.setRole(UserRole.ADMIN);
        return user;
    }

}
