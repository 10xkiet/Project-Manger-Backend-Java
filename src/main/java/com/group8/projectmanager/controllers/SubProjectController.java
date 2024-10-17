package com.group8.projectmanager.controllers;

import com.group8.projectmanager.dtos.project.ProjectCreateDto;
import com.group8.projectmanager.dtos.project.ProjectDetailDto;
import com.group8.projectmanager.dtos.project.ProjectSimpleDto;
import com.group8.projectmanager.dtos.project.ProjectUpdateDto;
import com.group8.projectmanager.services.ProjectService;
import com.group8.projectmanager.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{id}/")
@RequiredArgsConstructor
public class SubProjectController {

    private final UserService userService;
    private final ProjectService projectService;

    @GetMapping
    public ProjectDetailDto retrieveProject(@PathVariable long id) {
        return projectService.retrieveProjectDetail(id);
    }

    @GetMapping("/subprojects/")
    public List<ProjectSimpleDto> listSubProjects(@PathVariable long id) {
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
}

