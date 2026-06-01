package ru.matthew.NauJava.crypto.cipher;

import org.assertj.core.api.Assertions;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.matthew.NauJava.domain.crypto.algorithm.cipher.implementation.AdvancedEncryptionStandard;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;

import static ru.matthew.NauJava.crypto.utils.ConverterUtils.convertHexToByte;
import static ru.matthew.NauJava.crypto.utils.ConverterUtils.getEncryptedText;

public class AdvancedEncryptionStandardTest {

    private final AdvancedEncryptionStandard advancedEncryptionStandard = new AdvancedEncryptionStandard();

    @BeforeEach
    public void setUp() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @Test
    public void encrypt_WhenPlainTextEmpty_ReturnEmptyText() {
        byte[] key = convertHexToByte("4C973DBC7364621674F8B5B89E5C15511FCED9216490FB1C1A2CAA0FFE0407E5");
        byte[] iv = convertHexToByte("7AE8E2CA4EC500012E58495C");
        byte[] plainText = {};

        byte[] encryptedText = getEncryptedText(plainText, key, iv, advancedEncryptionStandard);

        Assertions.assertThat(encryptedText).isNotNull();
        Assertions.assertThat(encryptedText).hasSize(0);
    }

    @Test
    public void encrypt_WithTestVectorData_ReturnEncryptedData() {
        byte[] key = convertHexToByte("4C973DBC7364621674F8B5B89E5C15511FCED9216490FB1C1A2CAA0FFE0407E5");
        byte[] iv = convertHexToByte("7AE8E2CA4EC500012E58495C");
        byte[] plainText = convertHexToByte(
                "08000F101112131415161718191A1B1C1D" +
                        "1E1F202122232425262728292A2B2C2D" +
                        "2E2F303132333435363738393A3B3C3D" +
                        "3E3F404142434445464748490008");
        byte[] expectedText = convertHexToByte(
                "BA8AE31BC506486D6873E4FCE460E7DC" +
                        "57591FF00611F31C3834FE1C04AD80B6" +
                        "6803AFCF5B27E6333FA67C99DA47C2F0" +
                        "CED68D531BD741A943CFF7A6713BD0");

        byte[] encryptedText = getEncryptedText(plainText, key, iv, advancedEncryptionStandard);

        Assertions.assertThat(encryptedText).isNotNull();
        Assertions.assertThat(encryptedText).isEqualTo(expectedText);
    }

    @Test
    public void encryptAndDecryptData_ReturnDecryptedData() {
        byte[] plainText = "test plaint Text".getBytes(StandardCharsets.UTF_8);
        byte[] key = new byte[advancedEncryptionStandard.getSpec().getKeyLengthBit() / 8];
        byte[] iv = new byte[advancedEncryptionStandard.getSpec().getIvLengthByte()];

        SecretKey secretKey = new SecretKeySpec(key, advancedEncryptionStandard.getSpec().getName());

        byte[] encryptedText = advancedEncryptionStandard.encrypt(plainText, secretKey, iv);
        byte[] decryptedText = advancedEncryptionStandard.decrypt(encryptedText, secretKey, iv);

        Assertions.assertThat(decryptedText).isNotNull();
        Assertions.assertThat(decryptedText).isEqualTo(plainText);
    }
}
