package ru.matthew.NauJava.domain.password.dto;

public record PasswordEntryCreateDto (
        String login,
        char[] password,
        String serviceName,
        String description,
        String cipherSpec,
        String kdfSpec,
        int iterations
) {}
