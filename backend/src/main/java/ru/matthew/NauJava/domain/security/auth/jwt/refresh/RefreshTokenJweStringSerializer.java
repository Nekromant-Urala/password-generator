package ru.matthew.NauJava.domain.security.auth.jwt.refresh;

import com.nimbusds.jose.*;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

import java.util.Date;
import java.util.function.Function;

public class RefreshTokenJweStringSerializer implements Function<RefreshToken, String> {

    private final JWEEncrypter jweEncrypter;

    private JWEAlgorithm jweAlgorithm;

    private EncryptionMethod encryptionMethod = EncryptionMethod.A256GCM;

    public RefreshTokenJweStringSerializer(JWEEncrypter jweEncrypter) {
        this.jweEncrypter = jweEncrypter;
    }

    public RefreshTokenJweStringSerializer(JWEEncrypter jweEncrypter, JWEAlgorithm jweAlgorithm, EncryptionMethod encryptionMethod) {
        this.jweEncrypter = jweEncrypter;
        this.jweAlgorithm = jweAlgorithm;
        this.encryptionMethod = encryptionMethod;
    }

    @Override
    public String apply(RefreshToken refreshToken) { //TODO подумать на тем, чтобы использовать не jwe, а jws и в refresh-токене
        var jweHeader = new JWEHeader.Builder(jweAlgorithm, encryptionMethod)
                .keyID(refreshToken.id().toString())
                .build();
        var claimsSet = new JWTClaimsSet.Builder()
                .jwtID(refreshToken.id().toString())
                .subject(refreshToken.subject())
                .issueTime(Date.from(refreshToken.createdAt()))
                .expirationTime(Date.from(refreshToken.expiresAt()))
                .claim("authorities", refreshToken.authorities())
                .build();

        var encryptedJWT = new EncryptedJWT(jweHeader, claimsSet);
        try {
            encryptedJWT.encrypt(jweEncrypter);

            return encryptedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
            //TODO добавить логирование и обработку ошибки подписания
        }
    }

    public void setJweAlgorithm(JWEAlgorithm jweAlgorithm) {
        this.jweAlgorithm = jweAlgorithm;
    }

    public void setEncryptionMethod(EncryptionMethod encryptionMethod) {
        this.encryptionMethod = encryptionMethod;
    }
}
