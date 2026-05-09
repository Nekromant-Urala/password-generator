package ru.matthew.NauJava.domain.security.auth.jwt.access;

import ru.matthew.NauJava.domain.security.auth.jwt.refresh.RefreshToken;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

public class AccessTokenFactory implements Function<RefreshToken, AccessToken> {

    private Duration tokenTtl = Duration.ofMinutes(5);

    @Override
    public AccessToken apply(RefreshToken refreshToken) {
        var now = Instant.now();
        var authorities = refreshToken.authorities().stream()
                .filter(authority -> authority.startsWith("GRANT_"))
                .map(authority -> authority.replace("GRANT_", ""))
                .toList();

        return new AccessToken(refreshToken.id(), refreshToken.subject(), authorities, now, now.plus(tokenTtl));
    }

    public void setTokenTtl(Duration tokenTtl) {
        this.tokenTtl = tokenTtl;
    }
}
