package com.security.authentication.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
class  SecurityConfig{

    @Bean
    public SecurityFilterChain securityFilter(HttpSecurity http) throws Exception{
        String[] allowedUrl={"/health","/verify-token","/h2-console/**","/auth/signup","/auth/verify-otp","/auth/test","/auth/login"};
        http.authorizeHttpRequests(req->req.requestMatchers(allowedUrl)
                .permitAll().anyRequest().authenticated());
        http.sessionManagement(ses->ses
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.httpBasic(Customizer.withDefaults());
        http.csrf(csrf->csrf.disable());
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