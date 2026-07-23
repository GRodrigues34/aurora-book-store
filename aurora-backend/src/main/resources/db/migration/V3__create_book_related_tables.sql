CREATE TABLE book(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    price DECIMAL(10,2),
    image_url VARCHAR(255),
    stock INT NOT NULL DEFAULT 0
);

CREATE TABLE author(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE category(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE genre(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE book_author_relationship(
    book_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    FOREIGN KEY (book_id) REFERENCES book(id),
    FOREIGN KEY (author_id) REFERENCES author(id),
    PRIMARY KEY (book_id, author_id)
);

CREATE TABLE book_category_relationship(
    book_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    FOREIGN KEY (book_id) REFERENCES book(id),
    FOREIGN KEY (category_id) REFERENCES category(id),
    PRIMARY KEY (book_id, category_id)
);

CREATE TABLE book_genre_relationship(
  book_id BIGINT NOT NULL,
  genre_id BIGINT NOT NULL,
  FOREIGN KEY (book_id) REFERENCES book(id),
  FOREIGN KEY (genre_id) REFERENCES genre(id),
  PRIMARY KEY (book_id, genre_id)
);
