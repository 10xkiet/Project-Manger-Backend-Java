package com.group8.projectmanager.controllers;

import com.group8.projectmanager.dtos.DeleteUserDto;
import com.group8.projectmanager.dtos.PasswordChangeDto;
import com.group8.projectmanager.dtos.UserDto;
import com.group8.projectmanager.dtos.token.TokenObtainDto;
import com.group8.projectmanager.dtos.token.TokenRefreshDto;
import com.group8.projectmanager.repositories.UserRepository;
import com.group8.projectmanager.services.JwtsService;
import com.group8.projectmanager.services.ProjectService;
import com.group8.projectmanager.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/")
@RequiredArgsConstructor
public class UserController {

    private final JwtsService jwtsService;
    private final UserService userService;
    private final ProjectService projectService;
    private final UserRepository userRepository;

    @PostMapping("/register/")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@Valid @RequestBody UserDto dto) {

        var user = userService.createUser(dto);

        projectService.createProject(
            user,
            "Root project for " + user.getUsername(),
            null
        );
    }

    @PostMapping("/token/")
    public TokenObtainDto obtainToken(@Valid @RequestBody UserDto dto) {
        return jwtsService.tokenObtainPair(dto);
    }

    @PostMapping("/token/refresh/")
    public TokenRefreshDto refreshToken() {
        return jwtsService.refreshToken();
    }

    @PostMapping("/change-password/")
    public void changePassword(@Valid @RequestBody PasswordChangeDto dto) {
        userService.changePassword(dto);
    }

    @PostMapping("/delete/")
    public void deleteAccount(@Valid @RequestBody DeleteUserDto dto) {
        userService.deleteUser(dto);
    }
}