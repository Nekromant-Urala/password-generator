package ru.matthew.NauJava.domain.crypto.encrypt;

import ru.matthew.NauJava.domain.crypto.algorithm.cipher.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.KdfAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.exception.EncryptionException;

/**
 * Интерфейс сервиса для шифрования и расшифровывания данных
 */
public interface EncryptionService {

    /**
     * Метод для шифрования данных
     *
     * @param data            массив байт, которые необходимо зашифровать
     * @param masterPassword  мастер-пароль пользователя
     * @param cipherAlgorithm название алгоритма, которым необходимо зашифровывать данные (представлено в виде перечисления {@link CipherAlgorithmSpec})
     * @param keyGenerator    название алгоритма, которым необходимо создать секретный ключ (представлено в виде перечисления {@link KdfAlgorithmSpec})
     * @param iterations      количество итераций, которое необходимо совершить при шифровании
     * @return Возвращает зашифрованные данные в виде массива байт в кодировке Base64
     * @throws {@link EncryptionException} Выбрасывается при любой ошибке шифрования данных
     */
    byte[] encrypt(byte[] data, char[] masterPassword, CipherAlgorithmSpec cipherAlgorithm, KdfAlgorithmSpec keyGenerator, int iterations);

    /**
     * Метод для расшифровывания данных
     *
     * @param encryptedData   зашифрованные данные (в кодировке Base64), которые необходимо расшифровать
     * @param masterPassword  мастер-пароль пользователя
     * @param cipherAlgorithm название алгоритма, которым зашифровывались данные (представлено в виде перечисления {@link CipherAlgorithmSpec})
     * @param keyGenerator    название алгоритма, которым создавался секретный ключ (представлено в виде перечисления {@link KdfAlgorithmSpec})
     * @param iterations      количество итераций, при которых зашифровывались данные
     * @return Возвращает расшифрованные данные в виде массива байт
     * @throws {@link EncryptionException} Выбрасывается при любой ошибке расшифровки данных
     */
    byte[] decrypt(byte[] encryptedData, char[] masterPassword, CipherAlgorithmSpec cipherAlgorithm, KdfAlgorithmSpec keyGenerator, int iterations);
}
