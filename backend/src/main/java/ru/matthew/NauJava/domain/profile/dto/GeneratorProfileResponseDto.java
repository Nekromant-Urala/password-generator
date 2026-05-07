package ru.matthew.NauJava.domain.profile.dto;

import ru.matthew.NauJava.domain.crypto.algorithm.cipher.spec.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.KdfAlgorithmSpec;

import java.time.LocalDateTime;

public record GeneratorProfileResponseDto(
        Long id,
        String name,
        Integer passwordLength,
        boolean isUppercase,
        boolean isLowercase,
        boolean isDigits,
        boolean isSpecialChars,
        boolean isAvoidAmbiguousChars,
        boolean isFavorite,
        String customChars,
        LocalDateTime createAt,
        KdfAlgorithmSpec kdfAlgorithm,
        CipherAlgorithmSpec cipher
) {
}
