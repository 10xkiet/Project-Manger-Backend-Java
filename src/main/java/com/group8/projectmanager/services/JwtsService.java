package com.group8.projectmanager.services;

import com.group8.projectmanager.dtos.UserDto;
import com.group8.projectmanager.dtos.token.TokenObtainDto;
import com.group8.projectmanager.dtos.token.TokenRefreshResponseDto;
import com.group8.projectmanager.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtsService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    private final UserService userService;
    private final AuthenticationProvider authenticationProvider;

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


    public TokenObtainDto tokenObtainPair(UserDto dto) {

        var authentication = authenticationProvider.authenticate(
            new UsernamePasswordAuthenticationToken(
                dto.username(), dto.password()
            )
        );

        var user = userService.getUserByAuthentication(authentication).orElse(null);
        if (user == null) {
            throw new BadCredentialsException("Principal is not of User type.");
        }

        return new TokenObtainDto(
            generateToken(user, false).getTokenValue(),
            generateToken(user, true).getTokenValue()
        );
    }

    public TokenRefreshResponseDto refreshToken(String refresh) {

        var claim = new JwtAuthenticationToken(jwtDecoder.decode(refresh));
        var authentication = authenticationProvider.authenticate(claim);

        var user = userService.getUserByAuthentication(authentication)
            .orElseThrow();

        var newAccessToken = generateToken(user, false).getTokenValue();
        return new TokenRefreshResponseDto(newAccessToken);
    }
}
