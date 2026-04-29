package ru.matthew.NauJava.domain.user;

public record UserResponseDto(
        Long id,
        String username,
        String email,
        Role role
) {}
