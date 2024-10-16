package com.group8.projectmanager.controllers;

import com.group8.projectmanager.dtos.project.ProjectDetailDto;
import com.group8.projectmanager.dtos.project.ProjectSimpleDto;
import com.group8.projectmanager.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{id}/")
@RequiredArgsConstructor
public class SubProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ProjectDetailDto retrieveProject(@PathVariable long id) {
        return projectService.retrieveProject(id);
    }

    @GetMapping("/subprojects/")
    public List<ProjectSimpleDto> listSubProjects(@PathVariable long id) {
        return projectService.listSubProjects(id);
    }
}

