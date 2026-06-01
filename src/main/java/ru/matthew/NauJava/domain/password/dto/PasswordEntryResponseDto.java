package ru.matthew.NauJava.domain.password.dto;

import java.time.LocalDateTime;

public record PasswordEntryResponseDto (
        Long id,
        String login,
        char[] password,
        String serviceName,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
