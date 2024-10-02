package com.group8.projectmanager.controllers;

import com.group8.projectmanager.dtos.UserDto;
import com.group8.projectmanager.services.JwtsService;
import com.group8.projectmanager.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/")
@RequiredArgsConstructor
public class UserController {

    private final JwtsService jwtsService;
    private final UserService userService;

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