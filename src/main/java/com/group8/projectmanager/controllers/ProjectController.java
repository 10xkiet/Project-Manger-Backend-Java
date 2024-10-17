package com.group8.projectmanager.controllers;

import com.group8.projectmanager.dtos.project.ProjectCreateDto;
import com.group8.projectmanager.dtos.project.ProjectSimpleDto;
import com.group8.projectmanager.services.ProjectService;
import com.group8.projectmanager.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/")
public class ProjectController {

    private final UserService userService;
    private final ProjectService projectService;

    @GetMapping
    public List<ProjectSimpleDto> listRoots() {
        var user = userService.getUserByContext().orElseThrow();
        return projectService.listRootProjects(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProject(@Valid @RequestBody ProjectCreateDto dto) {
        var user = userService.getUserByContext().orElseThrow();
        projectService.createProject(
            user, null,
            dto.getName(), dto.getDescription()
        );
    }
}
