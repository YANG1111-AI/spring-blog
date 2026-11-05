package com.springblog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Static resources and public reads are open; every state-changing path requires a session and
 * a valid CSRF token.
 *
 * Authentication itself needs no wiring here: Spring Boot attaches the single UserDetailsService
 * and PasswordEncoder beans to the default provider once a SecurityFilterChain is defined.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_GET = {
            "/",
            "/page/**",
            "/article/{id:\\d+}",
            "/error",
            "/favicon.ico"
    };

    private static final String[] PUBLIC_RESOURCES = {
            "/assets/**",
            "/static/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/back/**",
            "/user/img/**",
            "/article_img/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(registry -> registry
                        .requestMatchers(HttpMethod.GET, PUBLIC_RESOURCES).permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET).permitAll()
                        .requestMatchers("/login", "/register", "/logout").permitAll()
                        // Per-user like/favourite state: the handler answers "needs login" for anonymous callers.
                        .requestMatchers(HttpMethod.GET, "/article/like/status/**", "/article/favorite/status/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,
                                "/article/like/**", "/article/unlike/**",
                                "/article/favorite/**", "/article/unfavorite/**",
                                "/comment").authenticated()
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());

        return http.build();
    }
}
