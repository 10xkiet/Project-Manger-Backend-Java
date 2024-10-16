package com.group8.projectmanager.services;

import com.group8.projectmanager.dtos.project.ProjectDetailDto;
import com.group8.projectmanager.dtos.project.ProjectSimpleDto;
import com.group8.projectmanager.models.Project;
import com.group8.projectmanager.models.User;
import com.group8.projectmanager.repositories.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ModelMapper modelMapper;
    private final UserService userService;
    private final ProjectRepository repository;

    public ProjectSimpleDto convertToDto(Project project) {
        return modelMapper.map(project, ProjectSimpleDto.class);
    }

    public ProjectDetailDto convertToDetailDto(Project project) {
        return modelMapper.map(project, ProjectDetailDto.class);
    }

    public boolean ableToView(Project project, User user) {

        var userId = user.getId();

        var ownerId = project.getCreator().getId();
        if (userId.equals(ownerId)) {
            return true;
        }

        var managerId = project.getManager().getId();
        if (managerId != null) {
            return userId.equals(managerId);
        } else {
            return false;
        }
    }

    private Project retrieveProjectAndCheck(long id) {

        Project target;

        try {
            target = repository.getReferenceById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        var user = userService.getUserByContext().orElseThrow();
        if (!this.ableToView(target, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return target;
    }

    @Transactional(readOnly = true)
    public ProjectDetailDto retrieveProject(long id) {
        var target = retrieveProjectAndCheck(id);
        return this.convertToDetailDto(target);
    }

    @Transactional(readOnly = true)
    public List<ProjectSimpleDto> listRootProjects(User user) {

        var userId = user.getId();

        return repository
            .findByCreatorIdAndManagerIdAndParentProjectNull(userId, userId)
            .map(this::convertToDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectSimpleDto> listSubProjects(long id) {

        var target = retrieveProjectAndCheck(id);

        return target.getSubProjects()
            .stream()
            .map(this::convertToDto)
            .toList();
    }


    public void createProject(User creator, String name, @Nullable String description) {

        var now = new Timestamp(System.currentTimeMillis());

        var builder = Project.builder()
            .name(name)
            .creator(creator)
            .createdOn(now);

        if (description != null) {
            builder.description(description);
        }

        repository.save(builder.build());
    }
}
