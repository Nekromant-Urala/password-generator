package ru.matthew.NauJava.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPatchDto(
        @NotBlank(message = "имя пользователя не может быть пустым")
        @Size(min = 1, max = 255, message = "имя пользователя должно содержать от 1 до 255 символов")
        String username,
        @Email(message = "некорректный формат почты")
        @Size(min = 1, max = 255, message = "длина электронной почты может быть от 1 до 255 символов")
        String email
) {
}
