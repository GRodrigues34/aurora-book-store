-- Additional Categories
INSERT IGNORE INTO category (id, name) VALUES (4, 'Philosophy');
INSERT IGNORE INTO category (id, name) VALUES (5, 'Modern Classics');
INSERT IGNORE INTO category (id, name) VALUES (6, 'Speculative Fiction');

-- Additional Genres
INSERT IGNORE INTO genre (id, name) VALUES (5, 'Magical Realism');
INSERT IGNORE INTO genre (id, name) VALUES (6, 'Historical Fiction');
INSERT IGNORE INTO genre (id, name) VALUES (7, 'Gothic');
INSERT IGNORE INTO genre (id, name) VALUES (8, 'Existentialism');
INSERT IGNORE INTO genre (id, name) VALUES (9, 'Space Opera');
INSERT IGNORE INTO genre (id, name) VALUES (10, 'Mystery');
INSERT IGNORE INTO genre (id, name) VALUES (11, 'Literary Fiction');
INSERT IGNORE INTO genre (id, name) VALUES (12, 'Post-Apocalyptic');

-- Additional Authors
INSERT IGNORE INTO author (id, name) VALUES (6, 'Frank Herbert');
INSERT IGNORE INTO author (id, name) VALUES (7, 'Ursula K. Le Guin');
INSERT IGNORE INTO author (id, name) VALUES (8, 'Gabriel Garcia Marquez');
INSERT IGNORE INTO author (id, name) VALUES (9, 'Fyodor Dostoevsky');
INSERT IGNORE INTO author (id, name) VALUES (10, 'Mary Shelley');
INSERT IGNORE INTO author (id, name) VALUES (11, 'Cormac McCarthy');
INSERT IGNORE INTO author (id, name) VALUES (12, 'Isaac Asimov');
INSERT IGNORE INTO author (id, name) VALUES (13, 'Ray Bradbury');
INSERT IGNORE INTO author (id, name) VALUES (14, 'Albert Camus');
INSERT IGNORE INTO author (id, name) VALUES (15, 'Virginia Woolf');
INSERT IGNORE INTO author (id, name) VALUES (16, 'Jorge Luis Borges');
INSERT IGNORE INTO author (id, name) VALUES (17, 'Margaret Atwood');
INSERT IGNORE INTO author (id, name) VALUES (18, 'Haruki Murakami');
INSERT IGNORE INTO author (id, name) VALUES (19, 'Machado de Assis');
INSERT IGNORE INTO author (id, name) VALUES (20, 'Umberto Eco');

-- 20 New Books (IDs 6..25)
INSERT INTO book (id, title, description, price, image_url, stock) VALUES
(6, 'Dune', 'A sweeping saga of politics, ecology, and destiny on the desert planet Arrakis.', 64.90, 'http://localhost:8080/covers/lotr.png', 18),
(7, 'The Left Hand of Darkness', 'An envoy navigates a frozen world where gender is fluid and diplomacy is survival.', 48.00, 'http://localhost:8080/covers/neuromancer.png', 14),
(8, 'One Hundred Years of Solitude', 'The multi-generational chronicle of the Buendia family in the mythical town of Macondo.', 54.90, 'http://localhost:8080/covers/hamlet.png', 20),
(9, 'Crime and Punishment', 'A psychological portrait of guilt, redemption, and moral reckoning in St. Petersburg.', 52.00, 'http://localhost:8080/covers/1984.png', 16),
(10, 'Frankenstein', 'The foundational gothic tale of ambition, creation, and the cost of playing god.', 34.90, 'http://localhost:8080/covers/hamlet.png', 22),
(11, 'The Road', 'A father and son walk through an ash-covered wasteland holding onto humanity.', 46.50, 'http://localhost:8080/covers/1984.png', 11),
(12, 'Foundation', 'Mathematician Hari Seldon predicts the fall of a galactic empire and plants a seed of knowledge.', 55.00, 'http://localhost:8080/covers/neuromancer.png', 19),
(13, 'Fahrenheit 451', 'In a future where books are burned, a fireman begins to question the ashes.', 38.90, 'http://localhost:8080/covers/brave-new-world.png', 25),
(14, 'The Stranger', 'A spare, haunting meditation on absurdity and detachment under the Mediterranean sun.', 36.00, 'http://localhost:8080/covers/brave-new-world.png', 17),
(15, 'Mrs. Dalloway', 'A single June day in London rendered through luminous stream-of-consciousness prose.', 41.00, 'http://localhost:8080/covers/hamlet.png', 13),
(16, 'Ficciones', 'Labyrinthine short stories of infinite libraries, mirrors, and philosophical puzzles.', 44.90, 'http://localhost:8080/covers/lotr.png', 15),
(17, 'The Handmaid''s Tale', 'A chilling portrait of totalitarian theocracy and quiet resistance.', 49.00, 'http://localhost:8080/covers/1984.png', 21),
(18, 'Kafka on the Shore', 'Two parallel journeys intertwine across talking cats, memory, and surreal fate.', 58.00, 'http://localhost:8080/covers/neuromancer.png', 12),
(19, 'The Posthumous Memoirs of Bras Cubas', 'A witty, irreverent Brazilian classic narrated from beyond the grave.', 37.50, 'http://localhost:8080/covers/hamlet.png', 24),
(20, 'The Name of the Rose', 'A medieval abbey becomes the stage for a cerebral murder mystery and theological intrigue.', 59.00, 'http://localhost:8080/covers/lotr.png', 10),
(21, 'The Dispossessed', 'A physicist travels between an anarchist moon and a capitalist twin planet.', 47.90, 'http://localhost:8080/covers/brave-new-world.png', 14),
(22, 'The Brothers Karamazov', 'Faith, doubt, and patricide collide in Dostoevsky''s towering final novel.', 68.00, 'http://localhost:8080/covers/1984.png', 9),
(23, 'Blood Meridian', 'A brutal, mythic anti-Western tracing violence across the American borderlands.', 56.00, 'http://localhost:8080/covers/brave-new-world.png', 8),
(24, 'The Martian Chronicles', 'Poetic linked stories of humanity''s colonization and haunting of Mars.', 43.00, 'http://localhost:8080/covers/neuromancer.png', 16),
(25, 'Dom Casmurro', 'Jealousy, unreliable memory, and enduring ambiguity in nineteenth-century Rio de Janeiro.', 35.90, 'http://localhost:8080/covers/hamlet.png', 27);

-- Relationships for Books 6..25
INSERT INTO book_author_relationship (book_id, author_id) VALUES
(6, 6), (7, 7), (8, 8), (9, 9), (10, 10),
(11, 11), (12, 12), (13, 13), (14, 14), (15, 15),
(16, 16), (17, 17), (18, 18), (19, 19), (20, 20),
(21, 7), (22, 9), (23, 11), (24, 13), (25, 19);

INSERT INTO book_category_relationship (book_id, category_id) VALUES
(6, 2), (6, 6),
(7, 2), (7, 6),
(8, 1), (8, 5),
(9, 1), (9, 4),
(10, 1), (10, 6),
(11, 5), (11, 6),
(12, 2), (12, 6),
(13, 1), (13, 2),
(14, 1), (14, 4),
(15, 1), (15, 5),
(16, 4), (16, 5),
(17, 5), (17, 6),
(18, 5), (18, 6),
(19, 1), (19, 4),
(20, 1), (20, 4),
(21, 2), (21, 4),
(22, 1), (22, 4),
(23, 5),
(24, 2), (24, 6),
(25, 1), (25, 5);

INSERT INTO book_genre_relationship (book_id, genre_id) VALUES
(6, 9), (6, 2),
(7, 2), (7, 11),
(8, 5), (8, 11),
(9, 8), (9, 11),
(10, 7), (10, 2),
(11, 12), (11, 11),
(12, 9), (12, 2),
(13, 3), (13, 2),
(14, 8), (14, 11),
(15, 11),
(16, 5), (16, 11),
(17, 3), (17, 11),
(18, 5), (18, 11),
(19, 11), (19, 8),
(20, 10), (20, 6),
(21, 2), (21, 8),
(22, 8), (22, 11),
(23, 6), (23, 11),
(24, 2), (24, 9),
(25, 11);
