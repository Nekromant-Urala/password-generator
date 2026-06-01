package ru.matthew.NauJava.domain.crypto.encrypt;

import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.spec.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.CipherFactory;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.SymmetricCipher;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.KdfAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.KdfFactory;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.SecretKeyGenerator;
import ru.matthew.NauJava.domain.crypto.exception.EncryptionException;
import ru.matthew.NauJava.domain.crypto.generation.RandomBytesGenerator;

import javax.crypto.SecretKey;
import java.nio.ByteBuffer;

@Service
public class EncryptionServiceImpl implements EncryptionService {

    private final CipherFactory cipherFactory;
    private final KdfFactory keyGeneratorFactory;
    private final RandomBytesGenerator randomBytesGenerator;

    @Autowired
    public EncryptionServiceImpl(CipherFactory cipherFactory, KdfFactory keyGeneratorFactory, RandomBytesGenerator randomBytesGenerator) {
        this.cipherFactory = cipherFactory;
        this.keyGeneratorFactory = keyGeneratorFactory;
        this.randomBytesGenerator = randomBytesGenerator;
    }

    @Override
    public byte[] encrypt(byte[] data, char[] masterPassword, CipherAlgorithmSpec cipherAlgorithm, KdfAlgorithmSpec keyGenerator) {
        try {
            SymmetricCipher cipher = cipherFactory.getCipher(cipherAlgorithm);
            SecretKeyGenerator secretKeyGenerator = keyGeneratorFactory.getSecretKeyGenerator(keyGenerator);

            byte[] salt = randomBytesGenerator.getRandomBytes(cipher.getSpec().getSaltLengthByte());
            byte[] iv = randomBytesGenerator.getRandomBytes(cipher.getSpec().getIvLengthByte());

            SecretKey key = secretKeyGenerator.generateSecretKey(cipher.getSpec(), masterPassword, salt, keyGenerator.getIterations());

            byte[] encryptedData = cipher.encrypt(data, key, iv);
            byte[] encryptedDataWithMeta = ByteBuffer.allocate(iv.length + salt.length + encryptedData.length)
                    .put(salt)
                    .put(iv)
                    .put(encryptedData)
                    .array();

            return Base64.encode(encryptedDataWithMeta);

        } catch (Exception e) {
            throw new EncryptionException(e);
        }
    }

    @Override
    public byte[] decrypt(byte[] encryptedData, char[] masterPassword, CipherAlgorithmSpec cipherAlgorithm, KdfAlgorithmSpec keyGenerator) {
        try {
            SymmetricCipher cipher = cipherFactory.getCipher(cipherAlgorithm);
            SecretKeyGenerator secretKeyGenerator = keyGeneratorFactory.getSecretKeyGenerator(keyGenerator);

            byte[] encryptedDataWithMeta = Base64.decode(encryptedData);
            ByteBuffer buffer = ByteBuffer.wrap(encryptedDataWithMeta);

            byte[] salt = new byte[cipher.getSpec().getSaltLengthByte()];
            buffer.get(salt);
            byte[] iv = new byte[cipher.getSpec().getIvLengthByte()];
            buffer.get(iv);
            byte[] encryptedDataWithoutMeta = new byte[buffer.remaining()];
            buffer.get(encryptedDataWithoutMeta);

            SecretKey secretKey = secretKeyGenerator.generateSecretKey(cipher.getSpec(), masterPassword, salt, keyGenerator.getIterations());

            return cipher.decrypt(encryptedDataWithoutMeta, secretKey, iv);

        } catch (Exception e) {
            throw new EncryptionException(e);
        }
    }
}
