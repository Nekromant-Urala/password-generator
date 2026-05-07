package ru.matthew.NauJava.domain.password.dto;

import ru.matthew.NauJava.domain.crypto.algorithm.cipher.spec.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.KdfAlgorithmSpec;

public record PasswordEntrySpecDto(
        char[] password,
        CipherAlgorithmSpec cipherSpec,
        KdfAlgorithmSpec kdfSpec,
        int iterations
) {}
