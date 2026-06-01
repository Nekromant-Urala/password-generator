package ru.matthew.NauJava.domain.profile.dto;

import java.time.LocalDateTime;

public record ProfileResponseDto(
        Long id,
        Long userId,
        String name,
        Integer passwordLength,
        boolean isUppercase,
        boolean isLowercase,
        boolean isDigits,
        boolean isSpecialChars,
        boolean isDuplicateChars,
        boolean isFavorite,
        String customChars,
        LocalDateTime createAt,
        String kdfAlgorithm,
        String cipher
) {
}
