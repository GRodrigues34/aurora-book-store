package com.github.gr.aurora_bookstore.tool;

import com.github.gr.aurora_bookstore.model.entity.Book;
import com.github.gr.aurora_bookstore.model.entity.Category;
import com.github.gr.aurora_bookstore.model.entity.Genre;
import com.github.gr.aurora_bookstore.repository.BookRepository;
import com.github.gr.aurora_bookstore.repository.CategoryRepository;
import com.github.gr.aurora_bookstore.repository.GenreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BorealTools {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final GenreRepository genreRepository;

    public BorealTools(BookRepository bookRepository,
            CategoryRepository categoryRepository,
            GenreRepository genreRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.genreRepository = genreRepository;
    }

    @Tool(description = "Search for books in the bookstore catalog. You can search by title, category, genre, or author name. The query MUST be in English.")
    public String searchBooks(Object query) {
        log.info("CALLED TOOL SEARCH BOOKS");
        log.info("TOOL SEARCHBOOKS CALLED WITH QUERY CLASS: {}", query != null ? query.getClass().getName() : "null");
        log.info("TOOL SEARCHBOOKS CALLED WITH QUERY='{}'", query);
        if (query == null) {
            return "Please provide a search query.";
        }

        String searchTerm = "";
        if (query instanceof String str) {
            searchTerm = str;
        } else if (query instanceof java.util.Map<?, ?> map) {
            Object qVal = map.get("query");
            if (qVal == null)
                qVal = map.get("title");
            if (qVal == null)
                qVal = map.get("author");
            if (qVal == null)
                qVal = map.get("genre");
            if (qVal == null)
                qVal = map.get("category");

            if (qVal != null) {
                if (qVal instanceof java.util.Map<?, ?> nestedMap) {
                    Object nestedVal = nestedMap.get("title");
                    if (nestedVal == null)
                        nestedVal = nestedMap.get("query");
                    searchTerm = (nestedVal != null) ? nestedVal.toString()
                            : nestedMap.values().iterator().next().toString();
                } else {
                    searchTerm = qVal.toString();
                }
            } else {
                searchTerm = map.values().stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(" "));
            }
        } else {
            searchTerm = query.toString();
        }

        log.info("NORMALIZING QUERY CALL");
        String normalized = normalize(searchTerm);
        log.info("NORMALIZED QUERY RESULT '{}'", normalized);

        if (normalized.isBlank()) {
            return "No books found matching the search criteria.";
        }

        // 1. Try search by Title
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(normalized);
        if (!books.isEmpty()) {
            log.info("FOUND BOOKS BY TITLE SEARCH RESULT '{}'", normalized);
            return formatBooks(books);
        }

        // 2. Try search by Category
        books = bookRepository.findByCategoriesNameContainingIgnoreCase(normalized);
        if (!books.isEmpty()) {
            log.info("FOUND BOOKS BY CATEGORY SEARCH RESULT '{}'", normalized);
            return formatBooks(books);
        }

        // 3. Try search by Genre
        books = bookRepository.findByGenresNameContainingIgnoreCase(normalized);
        if (!books.isEmpty()) {
            log.info("FOUND BOOKS BY GENRE SEARCH RESULT '{}'", normalized);
            return formatBooks(books);
        }

        // 4. Try search by Author
        books = bookRepository.findByAuthorsNameContainingIgnoreCase(normalized);
        if (!books.isEmpty()) {
            log.info("FOUND BOOKS BY AUTHOR SEARCH RESULT '{}'", normalized);
            return formatBooks(books);
        }

        log.info("NO BOOKS FOUND FOR '{}' USING ANY CRITERIA", normalized);
        return "No books found matching: " + searchTerm;
    }

    @Tool(description = "Get the list of all available search keywords, categories, and genres in the bookstore catalog.")
    public String searchKeywords() {
        log.info("CALLED TOOL SEARCH KEYWORDS");
        String categories = listCategories();
        String genres = listGenres();
        return "Available Categories: " + categories + "\nAvailable Genres: " + genres;
    }

    public String searchByCategory(String category) {
        log.info("CALLED METHOD SEARCH BY CATEGORY");
        List<Book> books = bookRepository.findByCategoriesNameContainingIgnoreCase(category);
        if (books.isEmpty()) {
            return "No books found in category: " + category;
        }
        return formatBooks(books);
    }

    public String searchByGenre(String genre) {
        log.info("CALLED METHOD SEARCH BY GENRE");
        List<Book> books = bookRepository.findByGenresNameContainingIgnoreCase(genre);
        if (books.isEmpty()) {
            return "No books found in genre: " + genre;
        }
        return formatBooks(books);
    }

    public String searchByTitle(String title) {
        log.info("CALLED METHOD SEARCH BY TITLE");
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);
        if (books.isEmpty()) {
            return "No books found containing title: " + title;
        }
        return formatBooks(books);
    }

    public String searchByAuthor(String author) {
        log.info("CALLED METHOD SEARCH BY AUTHOR");
        List<Book> books = bookRepository.findByAuthorsNameContainingIgnoreCase(author);
        if (books.isEmpty()) {
            return "No books found by author: " + author;
        }
        return formatBooks(books);
    }

    public String listCategories() {
        log.info("CALLED METHOD LIST CATEGORIES");
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            return "No categories found in the catalog.";
        }
        return categories.stream()
                .map(Category::getName)
                .collect(Collectors.joining(", "));
    }

    public String listGenres() {
        log.info("CALLED METHOD LIST GENRES");
        List<Genre> genres = genreRepository.findAll();
        if (genres.isEmpty()) {
            return "No genres found in the catalog.";
        }
        return genres.stream()
                .map(Genre::getName)
                .collect(Collectors.joining(", "));
    }

    private String normalize(String input) {
        log.info("NORMALIZING QUERY");
        if (input == null)
            return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase().trim();
    }

    private String formatBooks(List<Book> books) {
        log.info("FORMATTING BOOKS");
        return books.stream()
                .map(book -> String.format("- ID: %d | Title: %s | Price: R$ %.2f | Stock: %d | Description: %s",
                        book.getId(), book.getTitle(), book.getPrice(), book.getStock(), book.getDescription()))
                .collect(Collectors.joining("\n"));
    }
}
