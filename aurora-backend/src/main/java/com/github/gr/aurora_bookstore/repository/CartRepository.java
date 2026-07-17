package com.github.gr.aurora_bookstore.repository;

import com.github.gr.aurora_bookstore.model.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
}
