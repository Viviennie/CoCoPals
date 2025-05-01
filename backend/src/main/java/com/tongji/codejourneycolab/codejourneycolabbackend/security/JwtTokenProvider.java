package com.tongji.codejourneycolab.codejourneycolabbackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;

    public String generateToken(int id) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .claim("id", id)
                .expiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public int tryGetIdFromToken(String token) throws JwtException {
        var parser = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes())).build();
        Claims claims = parser.parseSignedClaims(token).getPayload();
        return claims.get("id", Integer.class);
    }

    public Date tryGetExpirationDateFromToken(String token) throws JwtException {
        var parser = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes())).build();
        Claims claims = parser.parseSignedClaims(token).getPayload();
        return claims.getExpiration();
    }

    private String generatePermanentToken(int id) {
        return Jwts.builder()
                .claim("id", id)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

}