package ru.matthew.NauJava.domain.user.dto;

import ru.matthew.NauJava.domain.user.Role;

public record UserResponseDto(
        Long id,
        String username,
        String email,
        Role role
) {}
