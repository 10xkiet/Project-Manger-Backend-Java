package com.group8.projectmanager.controllers;

import com.group8.projectmanager.dtos.GreetingDto;
import com.group8.projectmanager.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HelloController {

    private final UserService userService;

    @GetMapping("/api/hello/")
    public GreetingDto greeting() {

        var user = userService.getUserByContext();

        String message = "Hello, world!";
        if (user.isPresent()) {
            String username = user.get().getUsername();
            message = String.format("Hello, your username is %s.", username);
        }

        return new GreetingDto(message);
    }
}