package com.group8.projectmanager.config;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class JwtConfig {

    @Value("${jwts.secret-key}")
    private String secretKey;

    @Bean
    public SecretKey getSignature() {
        var bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    @Bean
    public JwtBuilder jwtBuilder() {
        return Jwts.builder().signWith(getSignature());
    }

    @Bean
    public JwtParser jwtParserBuilder() {
        return Jwts
                .parser()
                .verifyWith(getSignature())
                .build();
    }
}