package com.group8.projectmanager.controllers;

import com.group8.projectmanager.dtos.project.ProjectCreateDto;
import com.group8.projectmanager.dtos.project.ProjectDetailDto;
import com.group8.projectmanager.dtos.project.ProjectSimpleDto;
import com.group8.projectmanager.dtos.project.ProjectUpdateDto;
import com.group8.projectmanager.models.Project;
import com.group8.projectmanager.repositories.ProjectRepository;
import com.group8.projectmanager.services.ProjectService;
import com.group8.projectmanager.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{id}/")
@RequiredArgsConstructor
public class SubProjectController {

    private final ProjectService projectService;
    private final UserService userService ;
    private final ProjectRepository projectRepository;

    @GetMapping
    public ProjectDetailDto retrieveProject(@PathVariable long id) {
        return projectService.retrieveProject(id);
    }

    @GetMapping("/subprojects/")
    public List<ProjectSimpleDto> listSubProjects(@PathVariable long id) {
        return projectService.listSubProjects(id);
    }

    @PatchMapping("/new-subprojects/")
    public void newSubProjects(@PathVariable long id, @Valid @RequestBody ProjectCreateDto dto){
         var user= userService.getUserByContext().orElseThrow();
         var project=projectService.retrieveProjectAndCheck(id,user);
        var subProject= Project.builder().
                name(dto.getName()).
                description(dto.getDescription()).
                creator(user).build();
        project.getSubProjects().add(subProject);
        projectRepository.save(subProject);
        projectRepository.save(project);
        
    }

    @PatchMapping("/change-details/")
    void changeProjectDetails(@Valid @RequestBody ProjectUpdateDto dto, @PathVariable long id){
        var project=projectService.retrieveProjectAndCheck(id);
        project.setDeadline(dto.deadline());
        project.setName(dto.projectName());
        project.setDescription(dto.projectDesc());
        projectRepository.save(project);
    }

}

