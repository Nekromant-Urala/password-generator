package ru.matthew.NauJava.domain.user.dto;

public record UserUpdatePasswordDto(
        char[] password
) {
}
