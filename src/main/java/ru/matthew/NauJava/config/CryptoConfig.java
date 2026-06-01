package ru.matthew.NauJava.config;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.matthew.NauJava.domain.crypto.generation.RandomBytesGenerator;

import java.security.SecureRandom;
import java.security.Security;

@Configuration
public class CryptoConfig {

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    @Bean
    public RandomBytesGenerator randomBytesGenerator() {
        return (arrayLength) -> {
            byte[] randomBytes = new byte[arrayLength];
            new SecureRandom().nextBytes(randomBytes);
            return randomBytes;
        };
    }
}
