package com.github.gr.aurora_bookstore.repository;

import com.github.gr.aurora_bookstore.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByCategoriesNameContainingIgnoreCase(String name);
    List<Book> findByGenresNameContainingIgnoreCase(String name);
    List<Book> findByAuthorsNameContainingIgnoreCase(String name);
    List<Book> findByTitleContainingIgnoreCase(String title);
}
