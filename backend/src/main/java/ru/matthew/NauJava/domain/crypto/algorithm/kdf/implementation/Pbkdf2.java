package ru.matthew.NauJava.domain.crypto.algorithm.kdf.implementation;

import org.springframework.stereotype.Component;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.KdfAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.SecretKeyGenerator;
import ru.matthew.NauJava.domain.crypto.exception.EncryptionException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.spec.KeySpec;

import static ru.matthew.NauJava.domain.crypto.algorithm.kdf.Pbkdf2Spec.PBKDF_2;

@Component
public class Pbkdf2 implements SecretKeyGenerator {

    @Override
    public SecretKey generateSecretKey(CipherAlgorithmSpec spec, char[] keyWord, byte[] salt, int iterations) {
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBKDF_2.getMode());
            KeySpec keySpec = new PBEKeySpec(keyWord, salt, iterations, PBKDF_2.getKeyLengthBit());
            return new SecretKeySpec(keyFactory.generateSecret(keySpec).getEncoded(), spec.getName());
        } catch (Exception e) {
            throw new EncryptionException("Ошибка при создании секретного ключа с помощью PBKDF2. ", e);
        }
    }

    @Override
    public KdfAlgorithmSpec getSpec() {
        return PBKDF_2;
    }
}
