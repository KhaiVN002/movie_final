package com.nmquan1503.backend_springboot.configurations;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class SecurityConfig {
    static String[] PUBLIC_GET_ENDPOINTS = {
            "/provinces/**",
            "/districts/**",
            "/wards/**",
            "/genders/**",
            "/movies/**",
            "/payment-transactions/callback",
            "/movie-reviews/**",
            "/showtimes/movie/**",
            "/showtimes/detail/**",
            "/branches/**",
            "/room-types/**",
            "/movie-cast/**",
            "/movie-crew/**",
            "/movie-images/**",
            "/uploads/**"
    };

    static String[] PUBLIC_POST_ENDPOINTS = {
            "/users",
            "/auth/**",
            "/support-requests"
    };

    JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.exceptionHandling(exception -> exception.authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()
                                .requestMatchers(HttpMethod.POST, PUBLIC_POST_ENDPOINTS).permitAll()
                                .anyRequest().authenticated()
                );
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.cors(withDefaults -> {});
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
