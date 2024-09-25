package com.group8.projectmanager.controllers;

import com.group8.projectmanager.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/hello/")
@RequiredArgsConstructor
public class HelloController {

    private final UserService userService;

    @GetMapping
    public Map<String, String> greeting() {

        var authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        var user = userService.getUserByAuthentication(authentication);

        String message = "Hello, world!";
        if (user.isPresent()) {
            String username = user.get().getUsername();
            message = String.format("Hello, your username is %s.", username);
        }

        return Map.of("message", message);
    }
}
