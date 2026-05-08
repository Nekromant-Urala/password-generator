package ru.matthew.NauJava.domain.user.dto;

public record UserSingUpRequestDto(
        String username,
        String email,
        char[] password
) {}
