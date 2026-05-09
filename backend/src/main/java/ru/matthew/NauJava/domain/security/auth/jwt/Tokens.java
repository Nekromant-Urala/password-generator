package ru.matthew.NauJava.domain.security.auth.jwt;

public record Tokens(
        String accessToken,
        String accessTokenExpiry,
        String refreshToken,
        String refreshTokenExpiry
) {
}
