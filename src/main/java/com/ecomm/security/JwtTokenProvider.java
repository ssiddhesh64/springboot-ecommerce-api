package com.ecomm.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    @Value("${ecomm.jwt.secret:dGhpc2lzYW5leHRyZW1lbHlzdHJvbmdhbmRzZWN1cmVqd3RzZWNyZXRrZXlmb3JvdXJlc3ByaW5nYm9vdGVjb21tZXJjZWFwaXByb2plY3Q=}")
    private String secretKey;

    @Value("${ecomm.jwt.expiration:86400000}")
    private long expiration;

    public String generateToken(String email) {

        Date now = new Date();
        Date expireIn = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expireIn)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String getEmailFromToken(String token) {

        Claims claims =
                Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
