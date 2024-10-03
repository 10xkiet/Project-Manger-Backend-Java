package com.group8.projectmanager.converters;

import com.group8.projectmanager.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtToAuthernticationConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

    private final UserRepository userRepository;

    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt source) {

        var username = source.getSubject();
        var user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new UsernamePasswordAuthenticationToken(
            user, source, user.getAuthorities()
        );
    }
}
