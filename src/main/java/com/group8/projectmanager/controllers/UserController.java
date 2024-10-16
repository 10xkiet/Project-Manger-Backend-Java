package com.group8.projectmanager.controllers;

import com.group8.projectmanager.dtos.PasswordChangeDto;
import com.group8.projectmanager.dtos.UserDto;
import com.group8.projectmanager.repositories.UserRepository;
import com.group8.projectmanager.services.JwtsService;
import com.group8.projectmanager.services.ProjectService;
import com.group8.projectmanager.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/users/")
@RequiredArgsConstructor
public class UserController {

    private final JwtsService jwtsService;
    private final UserService userService;
    private final ProjectService projectService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/profile/{username}/")
    public String toProfile(@PathVariable String username) {

        var user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return user.getUsername() + "'s Profile";
    }

    @PostMapping("/register/")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@Valid @RequestBody UserDto dto) {

        var user = userService.createUser(dto);

        projectService.createProject(
            user, "Root project for " + user.getUsername(), null
        );
    }
    @PatchMapping("/profile/change-password")
    public void changePassword(@Valid @RequestBody PasswordChangeDto passwordChangeDto){
        var user = userService.getUserByContext();
        if(user.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");
        if(!passwordEncoder.matches(passwordChangeDto.OriginalPassword(), user.get().getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Incorrect password");
        if(!passwordChangeDto.ReconfirmNewPassword().equals(passwordChangeDto.NewPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"New password does not match");
        user.get().setPassword(passwordEncoder.encode(passwordChangeDto.NewPassword()));
        userRepository.save(user.get());

    }
    @DeleteMapping("profile/delete-account/")
    public void deleteUser(@Valid @RequestBody String password){
        var user=userService.getUserByContext();
        if(user.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");
        if(!passwordEncoder.matches(password,user.get().getPassword())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Incorrect Password");
        userRepository.deleteById(user.get().getId());
    }
    @PostMapping("/token/")
    public Map<String, String> obtainToken(@Valid @RequestBody UserDto dto) {
        return jwtsService.tokenObtainPair(dto);
    }
}