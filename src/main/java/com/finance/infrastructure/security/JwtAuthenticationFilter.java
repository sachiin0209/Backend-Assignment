package com.finance.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractTokenFromRequest(request);

            if (token != null && validateToken(token)) {
                String email = extractEmailFromToken(token);
                String role = extractRoleFromToken(token);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
                        );
                authentication.setDetails(email);

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Token validated for user: {}", email);
            }
        } catch (Exception e) {
            log.debug("Could not set user authentication in security context", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }

        return null;
    }

    /**
     * Validate JWT token
     */
    private boolean validateToken(String token) {
        try {
                Jwts.parser()
                        .verifyWith(getSigningKey())
                    .build()
                        .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.debug("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract email from token
     */
    private String extractEmailFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(getSigningKey())
                .build()
            .parseSignedClaims(token)
            .getPayload();
        return claims.getSubject();
    }

    /**
     * Extract role from token
     */
    private String extractRoleFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(getSigningKey())
                .build()
            .parseSignedClaims(token)
            .getPayload();
        return (String) claims.get("role");
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}
