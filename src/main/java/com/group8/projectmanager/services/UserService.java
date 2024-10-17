package com.group8.projectmanager.services;

import com.group8.projectmanager.dtos.DeleteUserDto;
import com.group8.projectmanager.dtos.PasswordChangeDto;
import com.group8.projectmanager.dtos.UserDto;
import com.group8.projectmanager.models.User;
import com.group8.projectmanager.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(UserDto dto) {

        if (repository.existsByUsername(dto.username())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Username already exists. Please try a different one."
            );
        }

        var hashedPassword = passwordEncoder.encode(dto.password());

        var user = User.builder()
            .username(dto.username())
            .password(hashedPassword)
            .build();

        return repository.save(user);
    }

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

    public Optional<User> getUserByContext() {

        var authentication = SecurityContextHolder
            .getContext()
            .getAuthentication();

        return this.getUserByAuthentication(authentication);
    }

    public void changePassword(PasswordChangeDto dto) {

        var user = getUserByContext().orElseThrow();
        if (!passwordEncoder.matches(dto.oldPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        var newHashedPassword = passwordEncoder.encode(dto.newPassword());
        user.setPassword(newHashedPassword);

        repository.save(user);
    }

    public void deleteUser(DeleteUserDto dto) {

        var user = getUserByContext().orElseThrow();

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        repository.delete(user);
    }
}
