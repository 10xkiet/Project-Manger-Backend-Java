package com.group8.projectmanager.controllers;

import com.group8.projectmanager.dtos.project.ProjectCreateDto;
import com.group8.projectmanager.dtos.project.ProjectDetailDto;
import com.group8.projectmanager.dtos.project.ProjectUpdateDto;
import com.group8.projectmanager.services.ProjectService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{id}/")
@SecurityRequirements({
    @SecurityRequirement(name = "basicAuth"),
    @SecurityRequirement(name = "bearerAuth")
})
@RequiredArgsConstructor
public class ProjectRetrieveController {

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
        @PathVariable("id") long parentId,
        @Valid @RequestBody ProjectCreateDto dto
    ) {
        projectService.newSubProject(parentId, dto);
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
    public void rotateCompleted(@PathVariable long id) {
        projectService.rotateCompleted(id);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable long id) {
        projectService.deleteProject(id);
    }
}