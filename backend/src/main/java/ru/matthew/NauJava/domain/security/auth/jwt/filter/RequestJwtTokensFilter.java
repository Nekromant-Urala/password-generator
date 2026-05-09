package ru.matthew.NauJava.domain.security.auth.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.matthew.NauJava.domain.security.auth.jwt.Tokens;
import ru.matthew.NauJava.domain.security.auth.jwt.access.AccessToken;
import ru.matthew.NauJava.domain.security.auth.jwt.access.AccessTokenFactory;
import ru.matthew.NauJava.domain.security.auth.jwt.refresh.RefreshToken;
import ru.matthew.NauJava.domain.security.auth.jwt.refresh.RefreshTokenFactory;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Objects;
import java.util.function.Function;

public class RequestJwtTokensFilter extends OncePerRequestFilter {

    private RequestMatcher requestMatcher = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/jwt/tokens");

    private SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();

    private Function<Authentication, RefreshToken> refreshTokenFactory = new RefreshTokenFactory();

    private Function<RefreshToken, AccessToken> accessTokenFactory = new AccessTokenFactory();

    private Function<RefreshToken, String> refreshTokenStringSerializer = Objects::toString;

    private Function<AccessToken, String> accessTokenStringSerializer = Objects::toString;

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(requestMatcher.matches(request)) {
            if(securityContextRepository.containsContext(request)) {
                var ctx = securityContextRepository.loadDeferredContext(request).get();
                if (ctx != null && !(ctx.getAuthentication() instanceof PreAuthenticatedAuthenticationToken)) {
                    var refreshToken = refreshTokenFactory.apply(ctx.getAuthentication());
                    var accessToken = accessTokenFactory.apply(refreshToken);

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    mapper.writeValue(
                            response.getWriter(),
                            new Tokens(
                                    accessTokenStringSerializer.apply(accessToken),
                                    accessToken.expiresAt().toString(),
                                    refreshTokenStringSerializer.apply(refreshToken),
                                    refreshToken.expiresAt().toString()
                            )
                    );
                    return;
                }
            }
            throw new AccessDeniedException("User must be auth");// TODO refactor message
        }
        filterChain.doFilter(request, response);
    }

    public void setRequestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }

    public void setSecurityContextRepository(SecurityContextRepository securityContextRepository) {
        this.securityContextRepository = securityContextRepository;
    }

    public void setRefreshTokenFactory(Function<Authentication, RefreshToken> refreshTokenFactory) {
        this.refreshTokenFactory = refreshTokenFactory;
    }

    public void setAccessTokenFactory(Function<RefreshToken, AccessToken> accessTokenFactory) {
        this.accessTokenFactory = accessTokenFactory;
    }

    public void setRefreshTokenStringSerializer(Function<RefreshToken, String> refreshTokenStringSerializer) {
        this.refreshTokenStringSerializer = refreshTokenStringSerializer;
    }

    public void setAccessTokenStringSerializer(Function<AccessToken, String> accessTokenStringSerializer) {
        this.accessTokenStringSerializer = accessTokenStringSerializer;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }
}
