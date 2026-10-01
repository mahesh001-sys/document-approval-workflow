package com.mahesh.daw.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "MaheshDocumentApprovalWorkflowSecretKey2026";

    private static final long EXPIRATION_TIME =
            1000 * 60 * 60; // 1 hour

    private final SecretKey key;

    public JwtService() {
        this.key = Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(UserDetails userDetails) {

        Date now = new Date();

        Date expiration = new Date(
                now.getTime() + EXPIRATION_TIME
        );

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }
    public String extractUsername(String token) {
    return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
}

public boolean isTokenValid(
        String token,
        UserDetails userDetails) {

    String username = extractUsername(token);

    return username.equals(userDetails.getUsername())
            && !isTokenExpired(token);
}

private boolean isTokenExpired(String token) {
    Date expiration = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getExpiration();

    return expiration.before(new Date());
}
}
