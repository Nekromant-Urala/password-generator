package ru.matthew.NauJava.crypto.utils;

import ru.matthew.NauJava.domain.crypto.algorithm.cipher.SymmetricCipher;


import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

public final class ConverterUtils {

    public static byte[] convertHexToByte(String input) {
        input = input.replaceAll("\\s", "");
        byte[] result = new byte[input.length() / 2];
        for (int i = 0; i < input.length(); i += 2) {
            result[i / 2] = (byte) Integer.parseInt(input.substring(i, i + 2), 16);
        }
        return result;
    }

    public static byte[] getEncryptedText(byte[] plainText, byte[] key, byte[] iv, SymmetricCipher cipher) {
        SecretKey secretKey = new SecretKeySpec(key, cipher.getSpec().getName());
        byte[] encryptedText = cipher.encrypt(plainText, secretKey, iv);
        return Arrays.copyOfRange(encryptedText, 0, encryptedText.length - 16);
    }
}
