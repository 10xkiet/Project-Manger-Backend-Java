package com.group8.projectmanager.controllers;

import com.group8.projectmanager.models.Project;
import com.group8.projectmanager.repositories.ProjectReposistory;
import com.group8.projectmanager.repositories.UserRepository;
import com.group8.projectmanager.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile/")
public class ProjectController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final ProjectReposistory projectReposistory;

    @GetMapping("/{userName}/")
    public String toProfile(@PathVariable("userName") String userName){
           var user = userRepository.findByUsername(userName).orElseThrow(()->
                   new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));
           return user.getUsername()+"'s Profile";
    }
    @PostMapping("/create-project/")
    @ResponseStatus(HttpStatus.CREATED)
    public void createProject(@RequestBody String projectName,@Nullable Authentication authentication){

           var user=userService.getUserByAuthentication(authentication).orElseThrow(()->
                   new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unallowed action"));
        Project project=new Project();
        project.setProjectName(projectName);
        project.setCreator(user);
        projectReposistory.save(project);
    }
}
