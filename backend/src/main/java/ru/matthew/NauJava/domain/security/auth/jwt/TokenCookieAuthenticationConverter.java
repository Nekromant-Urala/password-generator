package ru.matthew.NauJava.domain.security.auth.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import ru.matthew.NauJava.domain.security.auth.jwt.token.Token;

import java.util.function.Function;
import java.util.stream.Stream;

public class TokenCookieAuthenticationConverter implements AuthenticationConverter {

    private final Function<String, Token> tokenCookiesStringDeserializer;

    public TokenCookieAuthenticationConverter(Function<String, Token> tokenCookiesStringDeserializer) {
        this.tokenCookiesStringDeserializer = tokenCookiesStringDeserializer;
    }

    @Override
    public Authentication convert(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Stream.of(request.getCookies())
                    .filter(cookie -> cookie.getName().equals("__Host-auth-token"))
                    .findFirst()
                    .map(cookie -> {
                        var token = this.tokenCookiesStringDeserializer.apply(cookie.getValue());
                        return new PreAuthenticatedAuthenticationToken(token, cookie.getValue());
                    })
                    .orElse(null);
        }
        return null;
    }
}
