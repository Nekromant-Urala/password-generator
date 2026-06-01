package ru.matthew.NauJava.domain.password.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PasswordEntryRequestDto(
        @NotNull
        @Size(max = 255, message = "слишком длинный логин")
        String login,
        @NotNull
        char[] password,
        @NotNull
        @Size(max = 255, message = "слишком длинное имя сервиса")
        String serviceName,
        String description,
        @NotNull
        String profileName
) {}
