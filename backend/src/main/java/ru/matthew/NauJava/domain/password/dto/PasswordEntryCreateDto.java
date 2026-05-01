package ru.matthew.NauJava.domain.password.dto;

import ru.matthew.NauJava.domain.user.User;

public record PasswordEntryCreateDto (
        String login,
        char[] password,
        String serviceName,
        String description,
        User user
) {}
