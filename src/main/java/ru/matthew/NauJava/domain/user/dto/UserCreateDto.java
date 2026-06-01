package ru.matthew.NauJava.domain.user.dto;

import jakarta.validation.constraints.*;

public record UserCreateDto(
        @NotBlank(message = "имя пользователя не может быть пустым")
        @Size(min = 1, max = 255, message = "имя пользователя должно содержать от 1 до 255 символов")
        String username,
        @Email(message = "некорректный формат почты")
        @Size(min = 1, max = 255, message = "длина электронной почты может быть от 1 до 255 символов")
        String email,
        @NotNull
        @Size(min = 4, max = 30, message = "пароль должен содержать от 4 до 30 символов")
        char[] password
) {}
