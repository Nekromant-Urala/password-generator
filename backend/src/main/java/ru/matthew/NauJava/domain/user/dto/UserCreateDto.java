package ru.matthew.NauJava.domain.user.dto;

public record UserCreateDto(
        String username,
        String email,
        char[] password
) {}
