package ru.matthew.NauJava.domain.crypto.algorithm.kdf.implementation;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.springframework.stereotype.Component;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.KdfAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.SecretKeyGenerator;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static ru.matthew.NauJava.domain.crypto.algorithm.kdf.Argon2Spec.ARGON_2;

@Component
public class Argon2 implements SecretKeyGenerator {

    @Override //TODO посмотреть на то, что точно ли здесь именно строка, а не ENUM
    public SecretKey generateSecretKey(String algorithmName, char[] keyWord, byte[] salt, int iterations) {
        Argon2Parameters argon2Spec = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_i)
                .withVersion(Argon2Parameters.ARGON2_VERSION_13)
                .withIterations(iterations)
                .withMemoryAsKB(ARGON_2.getMemoryLimit())
                .withParallelism(ARGON_2.getParallelism())
                .withSalt(salt)
                .build();

        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(argon2Spec);

        byte[] key = new byte[ARGON_2.getHashLength()];
        generator.generateBytes(keyWord, key, 0, key.length);
        return new SecretKeySpec(key, algorithmName);
    }

    @Override
    public KdfAlgorithmSpec getSpec() {
        return ARGON_2;
    }
}
