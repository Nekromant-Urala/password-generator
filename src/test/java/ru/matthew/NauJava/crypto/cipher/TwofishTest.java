package ru.matthew.NauJava.crypto.cipher;

import org.assertj.core.api.Assertions;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.implementation.Twofish;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;

import static ru.matthew.NauJava.crypto.utils.ConverterUtils.convertHexToByte;
import static ru.matthew.NauJava.crypto.utils.ConverterUtils.getEncryptedText;

public class TwofishTest {

    private final Twofish twofish = new Twofish();

    @BeforeEach
    public void setUp() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @Test
    public void encrypt_WhenEmptyText_ReturnEmptyText() {
        byte[] key = convertHexToByte("4C973DBC7364621674F8B5B89E5C15511FCED9216490FB1C1A2CAA0FFE0407E5");
        byte[] iv = convertHexToByte("7AE8E2CA4EC500012E58495C");
        byte[] plainText = {};

        byte[] encryptedText = getEncryptedText(plainText, key, iv, twofish);

        Assertions.assertThat(encryptedText).isNotNull();
        Assertions.assertThat(encryptedText).hasSize(0);
    }

    @Test
    public void encryptAndDecryptData_ReturnDecryptedData() {
        byte[] plainText = "test plaint Text".getBytes(StandardCharsets.UTF_8);
        byte[] key = new byte[twofish.getSpec().getKeyLengthBit() / 8];
        byte[] iv = new byte[twofish.getSpec().getIvLengthByte()];

        SecretKey secretKey = new SecretKeySpec(key, twofish.getSpec().getName());

        byte[] encryptedText = twofish.encrypt(plainText, secretKey, iv);
        byte[] decryptedText = twofish.decrypt(encryptedText, secretKey, iv);

        Assertions.assertThat(decryptedText).isNotNull();
        Assertions.assertThat(decryptedText).isEqualTo(plainText);
    }
}
