package ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec;

/**
 * Интерфейс для получения конфигурации алгоритма Argon2
 */
public interface Argon2Configuration extends KdfAlgorithmSpec {
    int getMemoryLimit();

    int getHashLength();

    int getParallelism();
}
