package ru.matthew.NauJava.domain.security.auth.jwt.access;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.util.Date;
import java.util.function.Function;

public class AccessTokenJwsStringSerializer implements Function<AccessToken, String> {

    private final JWSSigner jwsSigner;

    private JWSAlgorithm jwsAlgorithm = JWSAlgorithm.HS256;

    public AccessTokenJwsStringSerializer(JWSSigner jwsSigner) {
        this.jwsSigner = jwsSigner;
    }

    public AccessTokenJwsStringSerializer(JWSSigner jwsSigner, JWSAlgorithm jwsAlgorithm) {
        this.jwsSigner = jwsSigner;
        this.jwsAlgorithm = jwsAlgorithm;
    }

    @Override
    public String apply(AccessToken accessToken) {
        var jwsHeader = new JWSHeader.Builder(jwsAlgorithm)
                .keyID(accessToken.id().toString())
                .build();
        var claimsSet = new JWTClaimsSet.Builder()
                .jwtID(accessToken.id().toString())
                .subject(accessToken.subject())
                .issueTime(Date.from(accessToken.createdAt()))
                .expirationTime(Date.from(accessToken.expiresAt()))
                .claim("authorities", accessToken.authorities())
                .build();

        var signedJWT = new SignedJWT(jwsHeader, claimsSet);
        try {
            signedJWT.sign(jwsSigner);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException();
            //TODO добавить логирование и обработку ошибки подписания
        }
    }

    public void setJwsAlgorithm(JWSAlgorithm jwsAlgorithm) {
        this.jwsAlgorithm = jwsAlgorithm;
    }
}
