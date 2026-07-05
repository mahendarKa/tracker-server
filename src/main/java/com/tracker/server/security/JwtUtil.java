package com.tracker.server.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(String username, Long userId) {

        Key key = new SecretKeySpec(
                secret.getBytes(),
                SignatureAlgorithm.HS256.getJcaName());

        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expiration))
                .signWith(key)
                .compact();
    }
    
//    private SecretKey getSignKey() {
//
//        byte[] keyBytes =
//                Decoders.BASE64.decode(
//                        secret);
//
//        return Keys.hmacShaKeyFor(
//                keyBytes);
//    }
    
    private SecretKey getSignKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes(
                        StandardCharsets.UTF_8));
    }
    
    
    private Claims extractClaims(
            String token) {

        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    private boolean isTokenExpired(
            String token) {

        return extractClaims(token)
                .getExpiration()
                .before(new Date());
    }
    
    public String extractUsername(
            String token) {

        return extractClaims(token)
                .getSubject();
    }

    public boolean validateToken(
            String token,
            String username) {

        return extractUsername(token)
                .equals(username)
                && !isTokenExpired(token);
    }
    public Long extractUserId(
            String token) {

        return extractClaims(token)
                .get("userId", Long.class);
    }
}