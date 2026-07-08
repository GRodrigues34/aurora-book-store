DELETE FROM users WHERE id = 1;
INSERT INTO users (id, username, email, password, role) 
VALUES (1, 'test_user', 'testemail@email.com', 'test_password', 'USER');
