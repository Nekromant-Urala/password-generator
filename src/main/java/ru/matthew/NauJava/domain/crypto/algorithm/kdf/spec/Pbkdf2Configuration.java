package ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec;

/**
 * Интерфейс для получения конфигурации алгоритма PBKDF2
 */
public interface Pbkdf2Configuration extends KdfAlgorithmSpec {
    int getKeyLengthBit();
}
