INSERT
    INTO users (username, email, password_hash, created_at, role)
        VALUES ('admin', 'admin@email.ru', '$2a$10$uH2UdonZcguWOB/Ls/hWT.GiJT6fyijjIWml.rf8wAaIKxovVrBaC', LOCALTIMESTAMP, 'ADMIN');
INSERT
    INTO generator_profile(name, password_length, include_uppercase, include_lowercase, include_digits, include_special_chars, avoid_ambiguous_chars, is_favorite, custom_chars, kdf_algorithm, cipher_algorithm, user_id, create_at)
        VALUES ('default', 12, true, true, true, true, false, true, '', 'PBKDF_2', 'TWOFISH', (SELECT id FROM users WHERE username='admin'), LOCALTIMESTAMP);
