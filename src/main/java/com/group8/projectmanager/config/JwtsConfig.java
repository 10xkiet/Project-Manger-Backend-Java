package com.group8.projectmanager.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class JwtsConfig {

    @Value("${jwts.secret-key}")
    private String secretKey;

    @Bean
    public SecretKey getSecretKey() {
        byte[] secretByte = Base64.getDecoder().decode(secretKey);
        return new SecretKeySpec(secretByte, "RSA");
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(getSecretKey()).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        var jwkSource = new ImmutableSecret<>(getSecretKey());
        return new NimbusJwtEncoder(jwkSource);
    }
}
