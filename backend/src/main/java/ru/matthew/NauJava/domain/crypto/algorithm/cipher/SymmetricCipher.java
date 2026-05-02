package ru.matthew.NauJava.domain.crypto.algorithm.cipher;

import ru.matthew.NauJava.domain.crypto.exception.EncryptionException;

import javax.crypto.SecretKey;

/**
 * Интерфейс для шифрования и расшифровывания данных
 */
public interface SymmetricCipher {

    /**
     * Метод предназначенный для шифрования данных
     *
     * @param byteArrayToEncrypt массив байт, которые необходимо зашифровать
     * @param key                ключ тип {@link SecretKey} необходимый для шифрования данных
     * @param iv                 случайная последовательность байт
     * @return Возвращает зашифрованные данные в виде массива байт
     * @throws {@link EncryptionException} Выбрасывается при любой ошибке шифрования данных
     */
    byte[] encrypt(byte[] byteArrayToEncrypt, SecretKey key, byte[] iv);

    /**
     * Метод предназначенный для расшифровывания данных
     *
     * @param byteArrayToDecrypt массив байт, которые необходимо расшифровать
     * @param key                ключ тип {@link SecretKey} необходимый для шифрования данных
     * @param iv                 случайная последовательность байт
     * @return Возвращает расшифрованные данные в виде массива байт
     * @throws {@link EncryptionException} Выбрасывается при любой ошибке расшифровывания данных
     */
    byte[] decrypt(byte[] byteArrayToDecrypt, SecretKey key, byte[] iv);

    /**
     * Получение конфигурации алгоритма
     *
     * @return Возвращает конфигурацию алгоритма {@link CipherAlgorithmSpec}
     */
    CipherAlgorithmSpec getSpec();
}
