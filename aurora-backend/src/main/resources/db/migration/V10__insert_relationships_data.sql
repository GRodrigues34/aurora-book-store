-- The Lord of the Rings: The Fellowship of the Ring
INSERT INTO book_author_relationship (book_id, author_id) VALUES (1, 1);
INSERT INTO book_category_relationship (book_id, category_id) VALUES (1, 1);
INSERT INTO book_category_relationship (book_id, category_id) VALUES (1, 3);
INSERT INTO book_genre_relationship (book_id, genre_id) VALUES (1, 1);

-- 1984
INSERT INTO book_author_relationship (book_id, author_id) VALUES (2, 2);
INSERT INTO book_category_relationship (book_id, category_id) VALUES (2, 1);
INSERT INTO book_category_relationship (book_id, category_id) VALUES (2, 2);
INSERT INTO book_genre_relationship (book_id, genre_id) VALUES (2, 3);

-- Brave New World
INSERT INTO book_author_relationship (book_id, author_id) VALUES (3, 3);
INSERT INTO book_category_relationship (book_id, category_id) VALUES (3, 1);
INSERT INTO book_category_relationship (book_id, category_id) VALUES (3, 2);
INSERT INTO book_genre_relationship (book_id, genre_id) VALUES (3, 3);

-- Neuromancer
INSERT INTO book_author_relationship (book_id, author_id) VALUES (4, 4);
INSERT INTO book_category_relationship (book_id, category_id) VALUES (4, 2);
INSERT INTO book_genre_relationship (book_id, genre_id) VALUES (4, 2);

-- Hamlet
INSERT INTO book_author_relationship (book_id, author_id) VALUES (5, 5);
INSERT INTO book_category_relationship (book_id, category_id) VALUES (5, 1);
INSERT INTO book_category_relationship (book_id, category_id) VALUES (5, 4);
INSERT INTO book_genre_relationship (book_id, genre_id) VALUES (5, 4);
INSERT INTO book_genre_relationship (book_id, genre_id) VALUES (5, 5);
