package com.finance.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.api.dto.common.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeHttpRequests(authorize -> authorize
                        // Public endpoints
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers("/actuator/**").permitAll()
                        // User endpoints - require authentication
                    .requestMatchers("/users/**").authenticated()
                        // Financial record endpoints - require authentication
                    .requestMatchers("/records/**").authenticated()
                        // Dashboard endpoints - require authentication
                    .requestMatchers("/dashboard/**").authenticated()
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    ErrorResponse errorResponse = new ErrorResponse(
                            HttpStatus.UNAUTHORIZED.value(),
                            "Unauthorized",
                            authException.getMessage(),
                            request.getRequestURI()
                    );
                    response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    ErrorResponse errorResponse = new ErrorResponse(
                            HttpStatus.FORBIDDEN.value(),
                            "Forbidden",
                            accessDeniedException.getMessage(),
                            request.getRequestURI()
                    );
                    response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
                });

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
