package com.example.user_service.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private final long jwtExpiration = 3600000; // 1 hour for access token

    @Value("${jwt.refreshExpiration}")
    private final long refreshExpiration = 86400000L; // 1 day for refresh token

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String consumerId) {
        return Jwts.builder()
                .subject(consumerId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(String consumerId) {
        return Jwts.builder()
                .subject(consumerId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }


    public Claims decodeToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // ✅ Ensure the correct SecretKey type is used
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public boolean isTokenValid(String token) {
        try {
            decodeToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
