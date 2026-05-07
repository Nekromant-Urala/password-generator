package ru.matthew.NauJava.domain.password.dto;

import ru.matthew.NauJava.domain.crypto.algorithm.cipher.spec.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.KdfAlgorithmSpec;
import ru.matthew.NauJava.domain.user.User;

public record PasswordEntryCreateDto (
        String login,
        char[] password,
        String serviceName,
        String description,
        User user,
        CipherAlgorithmSpec cipherSpec,
        KdfAlgorithmSpec kdfSpec,
        int iterations
) {}
