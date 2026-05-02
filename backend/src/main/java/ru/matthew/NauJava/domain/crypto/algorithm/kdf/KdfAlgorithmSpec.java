package ru.matthew.NauJava.domain.crypto.algorithm.kdf;

/**
 * Интерфейс для получения конфигураций реализованных KDF-алгоритмов
 */
public interface KdfAlgorithmSpec {
    String getName();
    String getMode();
}
