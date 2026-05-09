package ru.matthew.NauJava.domain.security.auth.jwt.refresh;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.SignedJWT;
import ru.matthew.NauJava.domain.security.auth.jwt.access.AccessToken;

import java.text.ParseException;
import java.util.UUID;
import java.util.function.Function;

public class RefreshTokenJweStringDeserializer implements Function<String, RefreshToken> {

    private final JWEDecrypter jweDecrypter;

    public RefreshTokenJweStringDeserializer(JWEDecrypter jweDecrypter) {
        this.jweDecrypter = jweDecrypter;
    }

    @Override
    public RefreshToken apply(String string) {
        try {

            var encryptedJWT = EncryptedJWT.parse(string);
            encryptedJWT.decrypt(jweDecrypter);
            var claimSet = encryptedJWT.getJWTClaimsSet();

            return new RefreshToken(
                    UUID.fromString(claimSet.getJWTID()),
                    claimSet.getSubject(),
                    claimSet.getStringListClaim("authorities"),
                    claimSet.getIssueTime().toInstant(),
                    claimSet.getExpirationTime().toInstant()
            );

        } catch (JOSEException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
