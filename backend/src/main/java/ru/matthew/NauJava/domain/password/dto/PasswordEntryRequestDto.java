package ru.matthew.NauJava.domain.password.dto;

public record PasswordEntryRequestDto(
        String login,
        char[] password,
        String serviceName,
        String description,
        String profileName
) {}
