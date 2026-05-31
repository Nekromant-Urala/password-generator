package ru.matthew.NauJava.crypto.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.CipherFactory;
import ru.matthew.NauJava.domain.crypto.algorithm.kdf.KdfFactory;
import ru.matthew.NauJava.domain.crypto.encrypt.EncryptionServiceImpl;
import ru.matthew.NauJava.domain.crypto.exception.CipherNotFoundException;
import ru.matthew.NauJava.domain.crypto.exception.EncryptionException;
import ru.matthew.NauJava.domain.crypto.exception.KdfNotFoundException;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.matthew.NauJava.domain.crypto.algorithm.cipher.spec.CipherAlgorithmSpec.*;
import static ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.Argon2Spec.ARGON_2;
import static ru.matthew.NauJava.domain.crypto.algorithm.kdf.spec.Pbkdf2Spec.PBKDF_2;

@ExtendWith(MockitoExtension.class)
public class EncryptionServiceTest {

    @Mock
    private KdfFactory kdfFactory;
    @Mock
    private CipherFactory cipherFactory;
    @InjectMocks
    private EncryptionServiceImpl encryptionService;

    @Test
    public void shouldThrowEncryptionException_WhenCipherNotFound() {
        when(cipherFactory.getCipher(any())).thenThrow(new CipherNotFoundException("Неизвестный алгоритм шифрования"));

        byte[] plainText = "plaintText".getBytes(StandardCharsets.UTF_8);
        char[] password = "pass".toCharArray();

        Assertions.assertThrows(
                EncryptionException.class,
                () -> encryptionService.encrypt(plainText, password, null, PBKDF_2)
        );
        Assertions.assertThrows(
                EncryptionException.class,
                () -> encryptionService.encrypt(plainText, password, null, ARGON_2)
        );
    }

    @Test
    public void shouldThrowEncryptionException_WhenKdfNotFound() {
        when(kdfFactory.getSecretKeyGenerator(any())).thenThrow(new KdfNotFoundException("Неизвестный алгоритм хеширования"));

        byte[] plainText = "plaintText".getBytes(StandardCharsets.UTF_8);
        char[] password = "pass".toCharArray();

        Assertions.assertThrows(
                EncryptionException.class,
                () -> encryptionService.encrypt(plainText, password, AES, null)
        );
        Assertions.assertThrows(
                EncryptionException.class,
                () -> encryptionService.encrypt(plainText, password, TWOFISH, null)
        );
        Assertions.assertThrows(
                EncryptionException.class,
                () -> encryptionService.encrypt(plainText, password, CHACHA20, null)
        );
    }

    @Test
    public void shouldThrowEncryptionException_WhenInputDataNull() {
        when(kdfFactory.getSecretKeyGenerator(any())).thenThrow(new KdfNotFoundException("Неизвестный алгоритм хеширования"));

        char[] password = "pass".toCharArray();

        Assertions.assertThrows(
                EncryptionException.class,
                () -> encryptionService.encrypt(null, password, AES, ARGON_2)
        );
    }

    @Test
    public void shouldThrowEncryptionException_WhenMasterPasswordNull() {
        when(kdfFactory.getSecretKeyGenerator(any())).thenThrow(new KdfNotFoundException("Неизвестный алгоритм хеширования"));

        byte[] plainText = "plaintText".getBytes(StandardCharsets.UTF_8);

        Assertions.assertThrows(
                EncryptionException.class,
                () -> encryptionService.encrypt(plainText, null, AES, ARGON_2)
        );
    }
}
