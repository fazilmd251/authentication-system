package com.security.authentication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF (usually needed for stateless JWT APIs)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Define which URLs are public
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/test", "/auth/**", "/h2-console/**").permitAll()
                        .anyRequest().authenticated() // Everything else is locked
                )

                // 3. Allow H2 Console to display in frames (if you use H2)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}