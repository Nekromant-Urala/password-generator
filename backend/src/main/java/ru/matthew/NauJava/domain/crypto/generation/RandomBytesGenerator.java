package ru.matthew.NauJava.domain.crypto.generation;

@FunctionalInterface
public interface RandomBytesGenerator {

    /**
     * Метод предназначенный для генерации последовательности случайных байт
     *
     * @param arrayLength длина массива
     * @return Возвращает случайную последовательность байт в виде массива байт
     */
    byte[] getRandomBytes(int arrayLength);
}
