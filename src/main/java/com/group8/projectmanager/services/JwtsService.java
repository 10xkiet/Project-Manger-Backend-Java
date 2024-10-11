package com.group8.projectmanager.services;

import com.group8.projectmanager.dtos.UserDto;
import com.group8.projectmanager.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtsService {

    private final JwtEncoder jwtEncoder;
    private final UserService userService;
    private final DaoAuthenticationProvider authenticationProvider;

    @Value("${jwts.access-token-lifetime}")
    private long accessTokenLifetime;

    @Value("${jwts.refresh-token-lifetime}")
    private long refreshTokenLifetime;

    private Jwt generateToken(User user, boolean isRefresh) {

        long lifetime = accessTokenLifetime;
        if (isRefresh) {
            lifetime = refreshTokenLifetime;
        }

        var issued = Instant.now();
        var expiration = issued.plusSeconds(lifetime);

        var claimsSet = JwtClaimsSet.builder()
            .subject(user.getUsername())
            .issuedAt(issued)
            .expiresAt(expiration)
            .build();

        var parameter = JwtEncoderParameters.from(claimsSet);

        return jwtEncoder.encode(parameter);
    }


    public Map<String, String> tokenObtainPair(UserDto dto) {

        var authentication = authenticationProvider.authenticate(
            new UsernamePasswordAuthenticationToken(
                dto.username(), dto.password()
            )
        );

        var user = userService.getUserByAuthentication(authentication).orElse(null);
        if (user == null) {
            throw new BadCredentialsException("Principal is not of User type.");
        }

        return Map.of(
            "access", generateToken(user, false).getTokenValue(),
            "refresh", generateToken(user, true).getTokenValue()
        );
    }
}
