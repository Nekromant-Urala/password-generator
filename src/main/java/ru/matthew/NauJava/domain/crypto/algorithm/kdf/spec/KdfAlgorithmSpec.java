package ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec;

/**
 * Интерфейс для получения конфигураций реализованных KDF-алгоритмов
 */
public interface KdfAlgorithmSpec {
    String getName();
    String getMode();
    int getIterations();
}
