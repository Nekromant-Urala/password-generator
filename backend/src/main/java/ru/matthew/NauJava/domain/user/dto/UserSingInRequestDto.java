package ru.matthew.NauJava.domain.user.dto;

public record UserSingInRequestDto(
        String username,
        char[] password
) {
}
