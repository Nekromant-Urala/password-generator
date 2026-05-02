package ru.matthew.NauJava.domain.crypto.algorithm.kdf;

import ru.matthew.NauJava.domain.crypto.algorithm.cipher.CipherAlgorithmSpec;

import javax.crypto.SecretKey;

public interface SecretKeyGenerator {
    /**
     * Метод предназначенный для генерации секретного ключа, с помощью которого происходит
     * дальнейшее шифрование данных в симметричных алгоритмах шифрования.
     *
     * @param spec       спецификация алгоритма шифрования, для которого создается секретный ключ
     * @param keyWord    ключевое слово для генерации ключа
     * @param salt       случайная последовательность байт, называемая "солью" для более надежной генерации ключа
     * @param iterations количество необходимых итераций, которые должен совершить алгоритма
     * @return Возвращает секретный ключ типа {@link SecretKey}
     */
    SecretKey generateSecretKey(CipherAlgorithmSpec spec, char[] keyWord, byte[] salt, int iterations);

    /**
     * Получение конфигурации алгоритма
     *
     * @return Возвращает конфигурацию алгоритма {@link KdfAlgorithmSpec}
     */
    KdfAlgorithmSpec getSpec();
}
