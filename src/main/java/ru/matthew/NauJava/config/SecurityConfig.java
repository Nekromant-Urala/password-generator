package ru.matthew.NauJava.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import static ru.matthew.NauJava.domain.user.Role.ADMIN;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String SIGN_IN = "/sign-in";
    private static final String LOGOUT = "/logout";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String SIGN_IN_SUCCESS_REDIRECT = "/passwords";
    private static final String SIGN_IN_ERROR = "/sign-in?error";
    private static final String SIGN_IN_EXPIRE = "/sign-in?expired";

    private static final String[] ADMIN_ONLY_REQUESTS = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api/v1/**",
            "/audit/**"
    };

    private static final String[] PERMIT_ALL_USER = {
            "/sign-in",
            "/sign-up",
            "/css/**",
            "/images/**",
            "/js/**"
    };

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        var handler = new SavedRequestAwareAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl(SIGN_IN_SUCCESS_REDIRECT);
        handler.setAlwaysUseDefaultTargetUrl(true);
        return handler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SessionRegistry sessionRegistry, AuthenticationSuccessHandler loginSuccessHandler) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PERMIT_ALL_USER).permitAll()
                        .requestMatchers(ADMIN_ONLY_REQUESTS).hasAuthority(ADMIN.name())
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage(SIGN_IN)
                        .loginProcessingUrl(SIGN_IN)
                        .successHandler(loginSuccessHandler)
                        .failureUrl(SIGN_IN_ERROR)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl(LOGOUT)
                        .logoutSuccessUrl(SIGN_IN)
                        .deleteCookies(JSESSIONID)
                        .invalidateHttpSession(true)
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .sessionRegistry(sessionRegistry)
                        .maxSessionsPreventsLogin(false)
                        .expiredUrl(SIGN_IN_EXPIRE)
                );

        return http.build();
    }
}
