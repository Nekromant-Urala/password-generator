package ru.matthew.NauJava.domain.security.auth.jwt.access;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.SignedJWT;
import ru.matthew.NauJava.domain.security.auth.jwt.Tokens;

import java.text.ParseException;
import java.util.UUID;
import java.util.function.Function;

public class AccessTokenJwsStringDeserializer implements Function<String, AccessToken> {

    private final JWSVerifier jwsVerifier;

    private JWSAlgorithm jwsAlgorithm = JWSAlgorithm.HS256;

    public AccessTokenJwsStringDeserializer(JWSVerifier jwsVerifier) {
        this.jwsVerifier = jwsVerifier;
    }

    public AccessTokenJwsStringDeserializer(JWSVerifier jwsVerifier, JWSAlgorithm jwsAlgorithm) {
        this.jwsVerifier = jwsVerifier;
        this.jwsAlgorithm = jwsAlgorithm;
    }

    @Override
    public AccessToken apply(String string) {
        try {
            var signedJWT = SignedJWT.parse(string);
            if (signedJWT.verify(jwsVerifier)) {
                var claimSet = signedJWT.getJWTClaimsSet();

                return new AccessToken(
                        UUID.fromString(claimSet.getJWTID()),
                        claimSet.getSubject(),
                        claimSet.getStringListClaim("authorities"),
                        claimSet.getIssueTime().toInstant(),
                        claimSet.getExpirationTime().toInstant()
                );
            }

        } catch (JOSEException | ParseException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void setJwsAlgorithm(JWSAlgorithm jwsAlgorithm) {
        this.jwsAlgorithm = jwsAlgorithm;
    }
}
