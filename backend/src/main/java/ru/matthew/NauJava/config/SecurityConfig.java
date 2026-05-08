package ru.matthew.NauJava.config;

import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.matthew.NauJava.domain.security.auth.jwt.TokenCookieAuthenticationConfigurer;
import ru.matthew.NauJava.domain.security.auth.jwt.TokenCookieJweStringDeserializer;
import ru.matthew.NauJava.domain.security.auth.jwt.TokenCookieJweStringSerializer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler((req, res, auth) -> res.setStatus(200))
                        .failureHandler((req, res, authEx) -> res.setStatus(401))
                );
        return http.build();
    }

    @Bean
    public TokenCookieJweStringSerializer tokenCookieJweStringSerializer(String cookieTokenKey) throws Exception {
        return new TokenCookieJweStringSerializer(
                new DirectEncrypter(OctetSequenceKey.parse(cookieTokenKey))
        );
    }

    @Bean
    public TokenCookieAuthenticationConfigurer tokenCookieAuthenticationConfigurer(String cookieTokenKey, JdbcTemplate jdbcTemplate) throws Exception {
        return new TokenCookieAuthenticationConfigurer()
                .tokenCookieStringDeserializer(
                        new TokenCookieJweStringDeserializer(
                                new DirectDecrypter(OctetSequenceKey.parse(cookieTokenKey))
                        ))
                .jdbcTemplate(jdbcTemplate);
    }


    @Bean
    public UserDetailsService userDetailsService(JdbcTemplate jdbcTemplate) {
        return username -> jdbcTemplate.query(
                "select * from t_user where c_username = ?",
                (rs, i) -> User.builder()
                        .username(rs.getString("c_username"))
                        .password(rs.getString("c_password"))
                        .authorities(
                                jdbcTemplate.query("select c_authority from t_user_authority where id_user = ?",
                                        (rs1, i1) -> new SimpleGrantedAuthority(rs1.getString("c_authority")),
                                        rs.getInt("id")
                                )
                        ).build(), username

        ).stream().findFirst().orElse(null);
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
