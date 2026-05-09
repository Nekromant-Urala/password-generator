package ru.matthew.NauJava.domain.security.auth.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.matthew.NauJava.domain.security.auth.jwt.TokenUser;
import ru.matthew.NauJava.domain.security.auth.jwt.Tokens;
import ru.matthew.NauJava.domain.security.auth.jwt.access.AccessToken;
import ru.matthew.NauJava.domain.security.auth.jwt.access.AccessTokenFactory;
import ru.matthew.NauJava.domain.security.auth.jwt.refresh.RefreshToken;

import java.io.IOException;

import java.util.Objects;
import java.util.function.Function;

public class RefreshTokenFilter extends OncePerRequestFilter {

    private Function<RefreshToken, AccessToken> accessTokenFactory = new AccessTokenFactory();

    private RequestMatcher requestMatcher = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/jwt/refresh");

    private Function<AccessToken, String> accessTokenJwsStringSerializer = Objects::toString;

    private SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (requestMatcher.matches(request)) {
            if (securityContextRepository.containsContext(request)) {
                var ctx = securityContextRepository.loadDeferredContext(request).get();
                if (ctx != null && ctx.getAuthentication() instanceof PreAuthenticatedAuthenticationToken
                        && ctx.getAuthentication().getPrincipal() instanceof TokenUser user
                        && ctx.getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("JWT_REFRESH"))) {
                    var accessToken = accessTokenFactory.apply(user.getRefreshToken());

                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    mapper.writeValue(
                            response.getWriter(),
                            new Tokens(
                                    accessTokenJwsStringSerializer.apply(accessToken),
                                    accessToken.expiresAt().toString(),
                                    null,
                                    null
                            )
                    );
                    return;
                }
            }
            throw new AccessDeniedException("User must be auth with JWT");
        }

        filterChain.doFilter(request, response);
    }

    public void setAccessTokenFactory(Function<RefreshToken, AccessToken> accessTokenFactory) {
        this.accessTokenFactory = accessTokenFactory;
    }

    public void setRequestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }

    public void setAccessTokenJwsStringSerializer(Function<AccessToken, String> accessTokenJwsStringSerializer) {
        this.accessTokenJwsStringSerializer = accessTokenJwsStringSerializer;
    }

    public void setSecurityContextRepository(SecurityContextRepository securityContextRepository) {
        this.securityContextRepository = securityContextRepository;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }
}
