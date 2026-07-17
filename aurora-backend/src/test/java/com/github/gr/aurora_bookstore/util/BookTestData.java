package com.github.gr.aurora_bookstore.util;

import com.github.gr.aurora_bookstore.model.entity.Author;
import com.github.gr.aurora_bookstore.model.entity.Book;
import com.github.gr.aurora_bookstore.model.entity.Category;
import com.github.gr.aurora_bookstore.model.entity.Genre;

import java.math.BigDecimal;
import java.util.Set;

public class BookTestData {

    public static Author createValidAuthor(){
        Author author = new Author();
        author.setId(1L);
        author.setName("testAuthor");
        return author;
    }

    public static Genre createValidGenre(){
        Genre genre = new Genre();
        genre.setId(1L);
        genre.setName("testGenre");
        return genre;
    }

    public static Category createValidCategory(){
        Category category = new Category();
        category.setId(1L);
        category.setName("testCategory");
        return category;
    }

    public static Book createValidBook(){
        Book book = new Book();
        book.setId(1L);
        book.setTitle("testBook");
        book.setDescription("testDescription");
        book.setPrice(BigDecimal.valueOf(1.0));
        book.setStock(1);
        book.setAuthors(Set.of(createValidAuthor()));
        book.setCategories(Set.of(createValidCategory()));
        book.setGenres(Set.of(createValidGenre()));
        return book;
    }

}
