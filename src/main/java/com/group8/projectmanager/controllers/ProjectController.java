package com.group8.projectmanager.controllers;

import com.group8.projectmanager.dtos.project.ProjectCreateDto;
import com.group8.projectmanager.dtos.project.ProjectDetailDto;
import com.group8.projectmanager.dtos.project.ProjectSimpleDto;
import com.group8.projectmanager.services.ProjectService;
import com.group8.projectmanager.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/")
public class ProjectController {

    private final UserService userService;
    private final ProjectService projectService;

    @GetMapping
    public List<ProjectSimpleDto> listRoots(Authentication authentication) {

        var user = userService.getUserByAuthentication(authentication)
            .orElseThrow();

        return projectService.listProjects(user);
    }

    @GetMapping("/{id}")
    public ProjectDetailDto retrieveProject(@PathVariable long id) {
        return projectService.retrieveProject(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProject(@Valid @RequestBody ProjectCreateDto dto) {

        var authentication = SecurityContextHolder
            .getContext()
            .getAuthentication();

        var user = userService.getUserByAuthentication(authentication)
            .orElseThrow();

        projectService.createProject(user, dto);
    }
}
