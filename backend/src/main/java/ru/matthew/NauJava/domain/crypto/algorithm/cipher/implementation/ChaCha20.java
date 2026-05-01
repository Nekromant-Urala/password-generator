package ru.matthew.NauJava.domain.crypto.algorithm.cipher.implementation;

import org.springframework.stereotype.Component;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.SymmetricCipher;
import ru.matthew.NauJava.domain.crypto.exception.EncryptionException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import static ru.matthew.NauJava.domain.crypto.algorithm.cipher.CipherAlgorithmSpec.CHACHA20;

@Component
public class ChaCha20 implements SymmetricCipher {

    @Override
    public byte[] encrypt(byte[] byteArrayToEncrypt, SecretKey key, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance(CHACHA20.getMode());
            GCMParameterSpec gcmSpec = new GCMParameterSpec(CHACHA20.getTagLengthBit(), iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);
            return cipher.doFinal(byteArrayToEncrypt);
        } catch (Exception e) {
            throw new EncryptionException("Ошибка при шифровании данных алгоритмом ChaCha20. ", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] byteArrayToDecrypt, SecretKey key, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance(CHACHA20.getMode());
            GCMParameterSpec gcmSpec = new GCMParameterSpec(CHACHA20.getTagLengthBit(), iv);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);
            return cipher.doFinal(byteArrayToDecrypt);
        } catch (Exception e) {
            throw new EncryptionException("Ошибка при расшифровывании данных алгоритмом ChaCha20. ", e);
        }
    }

    @Override
    public CipherAlgorithmSpec getSpec() {
        return CHACHA20;
    }
}
