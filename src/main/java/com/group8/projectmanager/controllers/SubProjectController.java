package com.group8.projectmanager.controllers;

import com.group8.projectmanager.dtos.project.ProjectCreateDto;
import com.group8.projectmanager.dtos.project.ProjectDetailDto;
import com.group8.projectmanager.dtos.project.ProjectUpdateDto;
import com.group8.projectmanager.models.ProjectType;
import com.group8.projectmanager.services.ProjectService;
import com.group8.projectmanager.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{id}/")
@SecurityRequirements({
    @SecurityRequirement(name = "basicAuth"),
    @SecurityRequirement(name = "bearerAuth")
})
@RequiredArgsConstructor
public class SubProjectController {

    private final UserService userService;
    private final ProjectService projectService;

    @GetMapping
    public ProjectDetailDto retrieveProject(@PathVariable long id) {
        return projectService.retrieveProjectDetail(id);
    }

    @GetMapping("/subprojects/")
    public List<ProjectDetailDto> listSubProjects(@PathVariable long id) {
        return projectService.listSubProjects(id);
    }

    @PostMapping("/subprojects/")
    @ResponseStatus(HttpStatus.CREATED)
    public void newSubProject(
        @PathVariable long id,
        @Valid @RequestBody ProjectCreateDto dto
    ) {

        var user = userService.getUserByContext().orElseThrow();

        var parentProject = projectService.retrieveProjectAndCheck(id, user);
        projectService.createProject(
            user, parentProject,
            dto.getName(), dto.getDescription()
        );
    }

    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeProjectInfo(
        @PathVariable long id,
        @Valid @RequestBody ProjectUpdateDto dto
    ) {
        projectService.changeProjectInfo(id, dto);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markCompleted(@PathVariable long id) {

        var target = projectService.retrieveProjectAndCheck(id);

        if (target.getType() != ProjectType.TASK) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Only task can mark completed"
            );
        }

        target.setIsCompleted(true);
    }
}