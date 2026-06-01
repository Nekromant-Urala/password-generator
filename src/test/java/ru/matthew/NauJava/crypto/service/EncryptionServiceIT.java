package ru.matthew.NauJava.crypto.service;

import org.assertj.core.api.Assertions;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.CipherFactory;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.SymmetricCipher;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.implementation.AdvancedEncryptionStandard;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.implementation.ChaCha20;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.implementation.Twofish;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.spec.CipherAlgorithmSpec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.KdfFactory;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.SecretKeyGenerator;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.implementation.Argon2;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.implementation.Pbkdf2;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.Argon2Spec;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.Pbkdf2Spec;
import ru.matthew.NauJava.domain.crypto.encrypt.EncryptionService;
import ru.matthew.NauJava.domain.crypto.encrypt.EncryptionServiceImpl;
import ru.matthew.NauJava.domain.crypto.generation.RandomBytesGenerator;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;
import java.util.List;
import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = EncryptionServiceIT.EncryptConfigurationTest.class)
public class EncryptionServiceIT {

    @Autowired
    private EncryptionService encryptionService;

    private static Stream<Arguments> cipherWithPbkdf2() {
        return Stream.of(
                Arguments.of(CipherAlgorithmSpec.AES, Pbkdf2Spec.PBKDF_2),
                Arguments.of(CipherAlgorithmSpec.TWOFISH, Pbkdf2Spec.PBKDF_2),
                Arguments.of(CipherAlgorithmSpec.CHACHA20, Pbkdf2Spec.PBKDF_2)
        );
    }

    private static Stream<Arguments> cipherWithArgon2() {
        return Stream.of(
                Arguments.of(CipherAlgorithmSpec.AES, Argon2Spec.ARGON_2),
                Arguments.of(CipherAlgorithmSpec.TWOFISH, Argon2Spec.ARGON_2),
                Arguments.of(CipherAlgorithmSpec.CHACHA20, Argon2Spec.ARGON_2)
        );
    }

    @ParameterizedTest
    @MethodSource("cipherWithPbkdf2")
    public void encryptAndDecrypt_ShouldWorkCorrectly_WithAnyCipherAndPbkdf2(
            CipherAlgorithmSpec cipherSpec,
            Pbkdf2Spec kdfSpec
    ) {
        byte[] plainText = "plainText".getBytes(StandardCharsets.UTF_8);
        char[] password = "pass".toCharArray();

        byte[] encryptedData = encryptionService.encrypt(plainText, password, cipherSpec, kdfSpec);

        Assertions.assertThat(encryptedData).isNotNull();
        Assertions.assertThat(encryptedData).isNotEmpty();

        byte[] decryptedData = encryptionService.decrypt(encryptedData, password, cipherSpec, kdfSpec);

        Assertions.assertThat(decryptedData).isNotNull();
        Assertions.assertThat(decryptedData).isEqualTo(plainText);
    }

    @ParameterizedTest
    @MethodSource("cipherWithArgon2")
    public void encryptAndDecrypt_ShouldWorkCorrectly_WithAnyCipherAndArgon2(
            CipherAlgorithmSpec cipherSpec,
            Argon2Spec kdfSpec
    ) {
        byte[] plainText = "plainText".getBytes(StandardCharsets.UTF_8);
        char[] password = "password".toCharArray();

        byte[] encryptedData = encryptionService.encrypt(plainText, password, cipherSpec, kdfSpec);

        Assertions.assertThat(encryptedData).isNotNull();
        Assertions.assertThat(encryptedData).isNotEmpty();

        byte[] decryptedData = encryptionService.decrypt(encryptedData, password, cipherSpec, kdfSpec);

        Assertions.assertThat(decryptedData).isNotNull();
        Assertions.assertThat(decryptedData).isEqualTo(plainText);
    }


    @TestConfiguration
    static class EncryptConfigurationTest {
        static {
            if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
                Security.addProvider(new BouncyCastleProvider());
            }
        }

        @Bean
        public SymmetricCipher advancedEncryptionStandard() {
            return new AdvancedEncryptionStandard();
        }

        @Bean
        public SymmetricCipher twofish() {
            return new Twofish();
        }

        @Bean
        public SymmetricCipher chaCha20() {
            return new ChaCha20();
        }

        @Bean
        public SecretKeyGenerator pbkdf2() {
            return new Pbkdf2();
        }

        @Bean
        public SecretKeyGenerator argon2() {
            return new Argon2();
        }

        @Bean
        public KdfFactory kdfFactory(List<SecretKeyGenerator> kdfList) {
            return new KdfFactory(kdfList);
        }

        @Bean
        CipherFactory cipherFactory(List<SymmetricCipher> cipherList) {
            return new CipherFactory(cipherList);
        }

        @Bean
        public RandomBytesGenerator randomBytesGenerator() {
            return (arrayLength) -> {
                byte[] randomBytes = new byte[arrayLength];
                new SecureRandom().nextBytes(randomBytes);
                return randomBytes;
            };
        }

        @Bean
        public EncryptionService encryptionService(KdfFactory kdfFactory, CipherFactory cipherFactory, RandomBytesGenerator generator) {
            return new EncryptionServiceImpl(cipherFactory, kdfFactory, generator);
        }
    }
}
