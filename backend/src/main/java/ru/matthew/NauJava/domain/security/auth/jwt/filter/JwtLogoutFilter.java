package ru.matthew.NauJava.domain.security.auth.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.matthew.NauJava.domain.security.auth.jwt.TokenUser;

import java.io.IOException;
import java.util.Date;

public class JwtLogoutFilter extends OncePerRequestFilter {

    private RequestMatcher requestMatcher = PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/jwt/logout");

    private SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();

    private final JdbcTemplate jdbcTemplate;

    public JwtLogoutFilter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (requestMatcher.matches(request)) {
            if (securityContextRepository.containsContext(request)) {
                var ctx = securityContextRepository.loadDeferredContext(request).get();
                if (ctx != null && ctx.getAuthentication() instanceof PreAuthenticatedAuthenticationToken
                        && ctx.getAuthentication().getPrincipal() instanceof TokenUser user
                        && ctx.getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("JWT_LOGOUT"))) {
                    jdbcTemplate.update(
                            "insert into t_diacrivate_token(id, c_keep_until) values( ?, ?)",
                            user.getRefreshToken().id(),
                            Date.from(user.getRefreshToken().expiresAt())
                    );
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    return;
                }
            }
            throw new AccessDeniedException("User must be auth with JWT");
        }
        filterChain.doFilter(request, response);
    }
}
