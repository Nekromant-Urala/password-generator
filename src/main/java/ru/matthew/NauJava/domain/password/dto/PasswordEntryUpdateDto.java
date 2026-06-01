package ru.matthew.NauJava.domain.password.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PasswordEntryUpdateDto(
        @NotNull
        @Size(max = 255, message = "слишком длинное имя сервиса")
        String serviceName,
        @NotNull
        @Size(max = 255, message = "слишком длинный логин")
        String login,
        char[] password,
        String description
) {
}
