package com.example.user_service.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;


import java.util.Date;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${jwt.expiration}")
    private final long jwtExpiration = 3600000; // 1 hour

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)); // ✅ Ensures correct key type
    }

    public String generateToken(String consumerId) {
        return Jwts.builder()
                .subject(consumerId) 
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), Jwts.SIG.HS256) // ✅ Updated in 0.12.6 (MacAlgorithm required)
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
