package ru.matthew.NauJava.domain.security.auth.jwt;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import ru.matthew.NauJava.domain.security.auth.jwt.access.AccessToken;
import ru.matthew.NauJava.domain.security.auth.jwt.refresh.RefreshToken;

import java.time.Instant;

public class TokenAuthenticationUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    private final JdbcTemplate jdbcTemplate;

    public TokenAuthenticationUserDetailsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken authenticationToken) throws UsernameNotFoundException {
        if(authenticationToken.getPrincipal() instanceof RefreshToken refreshToken) {
            return new TokenUser(
                    refreshToken.subject(),
                    "",
                    true,
                    true,
                    !jdbcTemplate.queryForObject("select exists(select id from t_deactivated_token where id = ?)", Boolean.class, refreshToken.id()) &&
                    refreshToken.expiresAt().isAfter(Instant.now()),
                    true,
                    refreshToken.authorities().stream().map(SimpleGrantedAuthority::new).toList(),
                    refreshToken
            );
        }
        throw new UsernameNotFoundException("Principal must be of type token");
    }
}
