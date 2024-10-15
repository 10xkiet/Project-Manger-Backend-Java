package com.group8.projectmanager.controllers;

import com.group8.projectmanager.dtos.UserDto;
import com.group8.projectmanager.repositories.UserRepository;
import com.group8.projectmanager.services.JwtsService;
import com.group8.projectmanager.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/users/")
@RequiredArgsConstructor
public class UserController {

    private final JwtsService jwtsService;
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/profile/{username}/")
    public String toProfile(@PathVariable String username) {

        var user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return user.getUsername() + "'s Profile";
    }

    @PostMapping("/register/")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@Valid @RequestBody UserDto dto) {
        userService.createUser(dto);
    }

    @PostMapping("/token/")
    public Map<String, String> obtainToken(@Valid @RequestBody UserDto dto) {
        return jwtsService.tokenObtainPair(dto);
    }
}