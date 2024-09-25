package com.group8.projectmanager.services;

import com.group8.projectmanager.dtos.UserDto;
import com.group8.projectmanager.models.User;
import com.group8.projectmanager.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtsService jwtsService;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationProvider authenticationProvider;

    public Optional<User>
    getUserByAuthentication(@Nullable Authentication authentication) {

        if (authentication == null) {
            return Optional.empty();
        }

        var principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    public void newUser(UserDto dto) {

        var hashedPassword = passwordEncoder.encode(dto.password());

        var user = User.builder()
                .username(dto.username())
                .password(hashedPassword)
                .build();

        repository.save(user);
    }

    public Map<String, String> tokenObtainPair(UserDto dto) {

        var authentication = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.username(), dto.password()
                )
        );

        var user = getUserByAuthentication(authentication).orElse(null);
        if (user == null) {
            String message = "Username or password doesn't match.";
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
        }

        return Map.of(
                "access", jwtsService.generateToken(user, false),
                "refresh", jwtsService.generateToken(user, true)
        );
    }
}