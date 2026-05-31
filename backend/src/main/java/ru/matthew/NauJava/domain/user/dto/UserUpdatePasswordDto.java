package ru.matthew.NauJava.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserUpdatePasswordDto(
        @NotNull
        char[] oldPassword,
        @NotNull
        @Size(min = 4, max = 30, message = "пароль должен содержать от 4 до 30 символов")
        char[] newPassword
) {
}
