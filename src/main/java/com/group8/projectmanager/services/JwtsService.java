package com.group8.projectmanager.services;

import com.group8.projectmanager.models.User;
import com.group8.projectmanager.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtsService {

    @Value("${jwts.access-token-lifetime}")
    private long accessTokenLifetime;

    @Value("${jwts.refresh-token-lifetime}")
    private long refreshTokenLifetime;

    private final JwtParser jwtParser;
    private final JwtBuilder jwtBuilder;

    private final UserRepository userRepository;

    private boolean isTokenExpired(Claims claims) {
        var now = new Date();
        return claims.getExpiration().before(now);
    }

    public Optional<User> getUserFromToken(String token) {

        Claims claims = jwtParser
                .parseSignedClaims(token)
                .getPayload();

        if (isTokenExpired(claims)) {
            return Optional.empty();
        }

        var username = claims.getSubject();
        return userRepository.findByUsername(username);
    }

    public String generateToken(User user, boolean isRefresh) {

        long lifetime = accessTokenLifetime;
        if (isRefresh) {
            lifetime = refreshTokenLifetime;
        }

        long issued = System.currentTimeMillis();
        long expiration = issued + lifetime;

        return jwtBuilder
                .claims(Map.of())
                .subject(user.getUsername())
                .issuedAt(new Date(issued))
                .expiration(new Date(expiration))
                .compact();
    }
}
