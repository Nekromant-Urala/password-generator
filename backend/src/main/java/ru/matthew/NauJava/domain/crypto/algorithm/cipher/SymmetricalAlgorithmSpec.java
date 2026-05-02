package ru.matthew.NauJava.domain.crypto.algorithm.cipher;

/**
 * Интерфейс для получения конфигураций симметричных алгоритмов шифрования.
 */
public interface SymmetricalAlgorithmSpec {
    String getName();

    String getMode();

    int getKeyLengthBit();

    int getTagLengthBit();

    int getIvLengthByte();

    int getSaltLengthByte();
}
