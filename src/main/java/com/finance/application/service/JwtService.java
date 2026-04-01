package com.finance.application.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Generate JWT token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Add role to claims
        String authorities = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .findFirst()
                .orElse("USER");
        claims.put("role", authorities);
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Generate JWT token with additional claims
     */
    public String generateToken(UserDetails userDetails, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>(extraClaims);
        // Add role to claims
        String authorities = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .findFirst()
                .orElse("USER");
        claims.put("role", authorities);
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Create JWT token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                    .claims(claims)
                    .subject(subject)
                    .issuedAt(now)
                    .expiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extract email from token
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract specific claim from token
     */
    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
                .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    /**
     * Check if token is valid
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String email = extractEmail(token);
            return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (Exception e) {
            log.debug("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if token is expired
     */
    private boolean isTokenExpired(String token) {
        try {
            final Date expiration = extractClaim(token, Claims::getExpiration);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.debug("Token expiration check failed: {}", e.getMessage());
            return true;
        }
    }
}
