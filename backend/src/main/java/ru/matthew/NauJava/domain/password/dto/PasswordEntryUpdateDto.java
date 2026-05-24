package ru.matthew.NauJava.domain.password.dto;

public record PasswordEntryUpdateDto(
        String serviceName,
        String login,
        char[] password,
        String description
) {
}
