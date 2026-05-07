package ru.matthew.NauJava.domain.profile.dto;

import ru.matthew.NauJava.domain.crypto.algorithm.cipher.spec.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.KdfAlgorithmSpec;
import ru.matthew.NauJava.domain.user.User;


public record GeneratorProfileCreateDto(
        String name,
        Integer passwordLength,
        boolean isUppercase,
        boolean isLowercase,
        boolean isDigits,
        boolean isSpecialChars,
        boolean isAvoidAmbiguousChars,
        boolean isFavorite,
        String customChars,
        User user,
        KdfAlgorithmSpec kdfAlgorithm,
        CipherAlgorithmSpec cipher
) {
}
