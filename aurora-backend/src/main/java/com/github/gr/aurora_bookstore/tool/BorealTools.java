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

    @Tool(description = "Search books by title, category, genre, or author. Query must be in English.")
    public String searchBooks(String query) {
        log.info("CALLED TOOL SEARCH BOOKS");
        log.info("TOOL SEARCHBOOKS CALLED WITH QUERY='{}'", query);
        if (query == null || query.isBlank()) {
            return "Please provide a search query.";
        }

        String cleaned = cleanQuery(query);
        log.info("CLEANED QUERY='{}'", cleaned);

        log.info("NORMALIZING QUERY CALL");
        String normalized = normalize(cleaned);
        log.info("NORMALIZED QUERY RESULT '{}'", normalized);

        if (normalized.isBlank()) {
            return "No books found matching the search criteria.";
        }

        // 1. Try search by Title
        String result = searchByTitle(normalized);
        if (!result.startsWith("No books found")) {
            log.info("BOOKS FOUND BY TITLE='{}'", normalized);
            return result;
        }

        // 2. Try search by Category
        result = searchByCategory(normalized);
        if (!result.startsWith("No books found")) {
            log.info("BOOKS FOUND BY CATEGORY='{}' USING ANY CRITERIA", normalized);
            return result;
        }

        // 3. Try search by Genre
        result = searchByGenre(normalized);
        if (!result.startsWith("No books found")) {
            log.info("BOOKS FOUND BY GENRE='{}' USING ANY CRITERIA", normalized);
            return result;
        }

        // 4. Try search by Author
        result = searchByAuthor(normalized);
        if (!result.startsWith("No books found")) {
            log.info("BOOKS FOUND BY AUTHOR='{}' USING ANY CRITERIA", normalized);
            return result;
        }

        log.info("NO BOOKS FOUND FOR '{}' USING ANY CRITERIA", normalized);
        return "No books found matching: " + query;
    }

    @Tool(description = "List all available categories and genres in the catalog.")
    public String searchCategoriesAndGenres() {
        log.info("CALLED TOOL SEARCH CATEGORIES AND GENRES");
        String categories = listCategories();
        String genres = listGenres();
        return "Available Categories: " + categories + "\nAvailable Genres: " + genres;
    }

    @Tool(description = "List all books in the catalog.")
    public String getAllBooks() {
        log.info("CALLED TOOL GET ALL BOOKS");
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) {
            return "No books found in the catalog.";
        }
        return formatBooks(books);
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

    private String cleanQuery(String query) {
        if (query == null) {
            return "";
        }
        // Remove prefix like "genre:", "author:", "title:", "name:" (with optional
        // colon or space) at the start of the query
        String cleaned = query.replaceAll("(?i)^(title|genre|author|name)\\s*[:\\s]\\s*", "");
        // Strip leading/trailing single/double quotes if the LLM wrapped it
        cleaned = cleaned.replaceAll("^['\"]|['\"]$", "");
        return cleaned.trim();
    }
}
