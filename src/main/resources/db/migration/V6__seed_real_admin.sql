DELETE FROM users WHERE email = 'admin@zioncenter.com';

INSERT IGNORE INTO users (name, email, password_hash, role, is_active)
VALUES ('Samwel Benjamin', 'samwelbenjaminsitta@gmail.com', '$2a$10$ZjAMxRPmO202DPjXBQ6l4OqZwyIyTOD8XGSxupj4OZPpBwO7iuNda', 'ADMIN', true);
