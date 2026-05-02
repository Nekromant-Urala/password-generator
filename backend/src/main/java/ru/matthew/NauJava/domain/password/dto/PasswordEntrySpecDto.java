package ru.matthew.NauJava.domain.password.dto;

import ru.matthew.NauJava.domain.crypto.algorithm.cipher.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.KdfAlgorithmSpec;

public record PasswordEntrySpecDto(
        char[] password,
        CipherAlgorithmSpec cipherSpec,
        KdfAlgorithmSpec kdfSpec,
        int iterations
) {}
