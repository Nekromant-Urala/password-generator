package ru.matthew.NauJava.common.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Утилитарный класс для преобразования данных между байтами, символами и строками.
 * <p>
 * Все преобразования в данном классе выполняются с использованием кодировки UTF-8.
 * Класс не предназначен для создания экземпляров.
 */
public final class ConverterUtils {

    private ConverterUtils() {
    }

    /**
     * Преобразует массив символов в массив байт, используя кодировку UTF-8.
     *
     * @param chars массив символов для преобразования.
     * @return новый массив байт, представляющий закодированные символы.
     * @throws IllegalArgumentException если переданный массив символов равен {@code null}.
     */
    public static byte[] charsToBytes(char[] chars) {
        if (chars == null) {
            throw new IllegalArgumentException("массив символов: null");
        }
        final ByteBuffer buffer = StandardCharsets.UTF_8.encode(CharBuffer.wrap(chars));
        return Arrays.copyOf(buffer.array(), buffer.limit());
    }

    /**
     * Преобразует массив байт в массив символов, используя кодировку UTF-8.
     *
     * @param bytes массив байт для декодирования.
     * @return новый массив символов, полученный из переданных байт.
     * @throws IllegalArgumentException если переданный массив байт равен {@code null}.
     */
    public static char[] bytesToChars(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("массив байт: null");
        }
        final CharBuffer buffer = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bytes));
        return Arrays.copyOf(buffer.array(), buffer.limit());
    }

    /**
     * Строго преобразует массив байт в строку с использованием кодировки UTF-8.
     * <p>
     * Метод работает в строгом режиме: при обнаружении поврежденных байт или
     * символов, которые невозможно отобразить, генерируется исключение.
     *
     * @param bytes массив байт для декодирования.
     * @return раскодированная строка.
     * @throws CharacterCodingException если данные повреждены или содержат недопустимые для UTF-8 последовательности.
     */
    public static String bytesToString(byte[] bytes) throws CharacterCodingException {
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();

        decoder.onMalformedInput(CodingErrorAction.REPORT);
        decoder.onUnmappableCharacter(CodingErrorAction.REPORT);

        CharBuffer charBuffer = decoder.decode(ByteBuffer.wrap(bytes));
        return charBuffer.toString();
    }
}
