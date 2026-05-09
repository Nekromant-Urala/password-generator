package ru.matthew.NauJava.domain.security.auth.jwt;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import ru.matthew.NauJava.domain.security.auth.jwt.access.AccessToken;
import ru.matthew.NauJava.domain.security.auth.jwt.filter.JwtLogoutFilter;
import ru.matthew.NauJava.domain.security.auth.jwt.filter.RefreshTokenFilter;
import ru.matthew.NauJava.domain.security.auth.jwt.filter.RequestJwtTokensFilter;
import ru.matthew.NauJava.domain.security.auth.jwt.refresh.RefreshToken;

import java.util.Objects;
import java.util.function.Function;

public class JwtAuthenticationConfigurer extends AbstractHttpConfigurer<JwtAuthenticationConfigurer, HttpSecurity> {

    private Function<RefreshToken, String> refreshTokenStringSerializer = Objects::toString;

    private Function<AccessToken, String> accessTokenStringSerializer = Objects::toString;

    private Function<String, AccessToken> accessTokenStringDeserializer;

    private Function<String, RefreshToken> refreshTokenStringDeserializer;

    private JdbcTemplate jdbcTemplate;

    @Override
    public void init(HttpSecurity builder) throws Exception {
        var csrfConfigurer = builder.getConfigurer(CsrfConfigurer.class);
        if (csrfConfigurer != null) {
            csrfConfigurer.ignoringRequestMatchers(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/jwt/tokens"));
        }
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        var requestJwtTokensFilter = new RequestJwtTokensFilter();
        requestJwtTokensFilter.setAccessTokenStringSerializer(accessTokenStringSerializer);
        requestJwtTokensFilter.setRefreshTokenStringSerializer(refreshTokenStringSerializer);

        var jwtAuthenticationFilter = new AuthenticationFilter(
                builder.getSharedObject(AuthenticationManager.class),
                new JwtAuthenticationConverter(accessTokenStringDeserializer, refreshTokenStringDeserializer)
        );
        jwtAuthenticationFilter.setSuccessHandler(
                ((request, response, authentication) -> CsrfFilter.skipRequest(request))
        );
        jwtAuthenticationFilter.setFailureHandler(
                ((request, response, exception) -> response.sendError(HttpServletResponse.SC_FORBIDDEN))
        );
        var preAuthenticatedAuthenticationProvider = new PreAuthenticatedAuthenticationProvider();
        preAuthenticatedAuthenticationProvider.setPreAuthenticatedUserDetailsService(new TokenAuthenticationUserDetailsService(jdbcTemplate));

        var refreshTokenFilter = new RefreshTokenFilter();
        refreshTokenFilter.setAccessTokenJwsStringSerializer(accessTokenStringSerializer);

        var jwtLogoutFilter = new JwtLogoutFilter(jdbcTemplate);


        builder
                .addFilterAfter(requestJwtTokensFilter, ExceptionTranslationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, CsrfFilter.class)
                .addFilterBefore(refreshTokenFilter, ExceptionTranslationFilter.class)
                .addFilterBefore(jwtLogoutFilter, ExceptionTranslationFilter.class)
                .authenticationProvider(preAuthenticatedAuthenticationProvider);
    }

    public JwtAuthenticationConfigurer refreshTokenStringSerializer(Function<RefreshToken, String> refreshTokenStringSerializer) {
        this.refreshTokenStringSerializer = refreshTokenStringSerializer;
        return this;
    }

    public JwtAuthenticationConfigurer accessTokenStringSerializer(Function<AccessToken, String> accessTokenStringSerializer) {
        this.accessTokenStringSerializer = accessTokenStringSerializer;
        return this;
    }

    public JwtAuthenticationConfigurer accessTokenStringDeserializer(Function<String, AccessToken> accessTokenStringDeserializer) {
        this.accessTokenStringDeserializer = accessTokenStringDeserializer;
        return this;
    }

    public JwtAuthenticationConfigurer refreshTokenStringDeserializer(Function<String, RefreshToken> refreshTokenStringDeserializer) {
        this.refreshTokenStringDeserializer = refreshTokenStringDeserializer;
        return this;
    }

    public JwtAuthenticationConfigurer jdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        return this;
    }
}
