package com.security.authentication.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
class  SecurityConfig{

    @Bean
    public SecurityFilterChain securityFilter(HttpSecurity http) throws Exception{
        String[] allowedUrl={"/auth/reset-password","/auth/verify-reset-password","/auth/forgot-password","/refresh","/health","/verify-token","/h2-console/**","/auth/signup","/auth/verify-otp","/auth/test","/auth/login"};
        http.authorizeHttpRequests(req->req.requestMatchers(allowedUrl)
                .permitAll().anyRequest().authenticated());
        http.sessionManagement(ses->ses
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.httpBasic(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();

            // Allow Angular frontend
            config.setAllowedOrigins(Arrays.asList(
                    "http://localhost:4200",
                    "http://127.0.0.1:4200"
            ));

            // All HTTP methods
            config.setAllowedMethods(Arrays.asList(
                    "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
            ));

            // All common headers
            config.setAllowedHeaders(Arrays.asList("*"));

            // Expose headers to frontend
            config.setExposedHeaders(Arrays.asList(
                    "Authorization", "Content-Type"
            ));

            // Allow cookies/auth headers
            config.setAllowCredentials(true);

            // Preflight cache 1 hour
            config.setMaxAge(3600L);

            return config;
        }));
        http.headers(h->h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}




//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
////@EnableWebSecurity
//public class SecurityConfig {
////    @Bean
////    public WebSecurityCustomizer webSecurityCustomizer() {
////        return (web) -> web.ignoring().requestMatchers("/h2-console/**");
////    }
//
////    @Bean
////    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
////        http
////                // 1. Disable CSRF (usually needed for stateless JWT APIs)
////                .csrf(AbstractHttpConfigurer::disable)
////
////                // 2. Define which URLs are public
////                .authorizeHttpRequests(auth -> auth
////                        .requestMatchers("/test", "/auth/**", "/h2-console/**").permitAll()
////                        .anyRequest().authenticated() // Everything else is locked
////                )
////
////                // 3. Allow H2 Console to display in frames (if you use H2)
////                .headers(headers -> headers.frameOptions(frame -> frame.disable()));
////
////        return http.build();
////    }
//
////    @Bean
////    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
////        http
////                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF
////                .authorizeHttpRequests(auth -> auth
////                        .anyRequest().permitAll() // 🔓 Everything is now wide open
////                )
////                .headers(headers -> headers.frameOptions(f -> f.disable())); // For H2 Console
////
////        return http.build();
////    }
////    @Bean
////    public PasswordEncoder passwordEncoder(){
////        return new BCryptPasswordEncoder();
////    }
//}