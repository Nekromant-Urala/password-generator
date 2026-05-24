package ru.matthew.NauJava.domain.user.dto;

public record UserLoginDto(
        String email,
        char[] password
) {
}
