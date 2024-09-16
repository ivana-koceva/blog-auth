package com.scalefocus.auth.service;

import com.scalefocus.auth.model.exception.TokenNotValidException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "GEX9cjU8ofontLwZgaqGejJiTgLSxC3D9CDn4voa7i/vmc7oUBa1UO6hUL0WVFIl";
    private final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private Claims extractAllClaims(String token) {
        logger.trace("Extracting all claims from token {}", token);
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
        catch (Exception e) {
            throw new TokenNotValidException();
        }
    }

    private Key getSignInKey() {
        logger.trace("Extracting sign in key from token");
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean isTokenExpired(String token) {
        logger.trace("Checking if token is expired {}", token);
        return !extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        logger.trace("Extracting expiration time from token {}", token);
        return extractClaim(token, Claims::getExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        logger.trace("Building token");
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(UserDetails userDetails) {
        logger.trace("Generating token");
        return buildToken(new HashMap<>(), userDetails);
    }

    public String extractUsername(String jwt) {
        logger.trace("Extracting username from token {}", jwt);
        return extractClaim(jwt, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        logger.trace("Extracting claims from token {}", token);
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        logger.trace("Checking if user token is valid {}", token);
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && isTokenExpired(token);
    }

    public boolean isTokenValid(String token) {
        logger.trace("Checking if token is valid {}", token);
        return isTokenExpired(token);
    }
}
