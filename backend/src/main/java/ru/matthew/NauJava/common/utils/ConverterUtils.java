package ru.matthew.NauJava.common.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class ConverterUtils {

    private ConverterUtils() {
    }

    public static byte[] charsToBytes(char[] chars) {
        final ByteBuffer buffer = StandardCharsets.UTF_8.encode(CharBuffer.wrap(chars));
        return Arrays.copyOf(buffer.array(), buffer.limit());
    }

    public static char[] bytesToChars(byte[] bytes) {
        final CharBuffer buffer = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bytes));
        return Arrays.copyOf(buffer.array(), buffer.limit());
    }
}
