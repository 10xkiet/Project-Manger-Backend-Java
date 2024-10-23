package com.group8.projectmanager.services;

import com.group8.projectmanager.dtos.project.ProjectCreateDto;
import com.group8.projectmanager.dtos.project.ProjectDetailDto;
import com.group8.projectmanager.dtos.project.ProjectSimpleDto;
import com.group8.projectmanager.dtos.project.ProjectUpdateDto;
import com.group8.projectmanager.models.Project;
import com.group8.projectmanager.models.ProjectType;
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

    private ProjectSimpleDto convertToDto(Project project) {
        return modelMapper.map(project, ProjectSimpleDto.class);
    }

    private boolean computeCompleted(Project project) {

        if (project.getType() == ProjectType.TASK) {
            return project.getIsCompleted();
        }

        if (project.getIsCompleted()) {
            return true;
        }

        boolean allCompleted = project.getSubProjects()
            .stream()
            .allMatch(this::computeCompleted);

        if (allCompleted) {

            project.setIsCompleted(true);
            repository.save(project);

            return true;

        } else {
            return false;
        }
    }

    private ProjectDetailDto convertToDetailDto(Project project) {

        var result = new ProjectDetailDto();

        modelMapper.map(project, result);

        computeCompleted(project);
        var completed = repository.countByIdAndSubProjectsIsCompletedTrue(project.getId());
        result.setCompletedCount(completed);

        result.setCreator(project.getCreator().getUsername());

        var manager = project.getManager();
        if (manager != null) {
            result.setManager(manager.getUsername());
        }

        var parent = project.getParentProject();
        if (parent != null) {
            result.setParentProjectId(parent.getId());
        }

        result.setSubProjectCount(project.getSubProjects().size());

        return result;
    }

    public boolean ableToView(Project project, User user) {

        var userId = user.getId();

        var ownerId = project.getCreator().getId();
        if (userId.equals(ownerId)) {
            return true;
        }

        var manager = project.getManager();
        if (manager != null) {
            return userId.equals(manager.getId());
        } else {
            return false;
        }
    }

    public Project retrieveProjectAndCheck(long id, User user) {

        Project target;

        try {
            target = repository.getReferenceById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (!this.ableToView(target, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return target;
    }

    public Project retrieveProjectAndCheck(long id) {
        var user = userService.getUserByContext().orElseThrow();
        return this.retrieveProjectAndCheck(id, user);
    }

    public void createProject(
        User creator, @Nullable Project parentProject,
        String name, @Nullable String description
    ) {

        if (parentProject != null) {

            parentProject.setType(ProjectType.PROJECT);
            parentProject.setIsCompleted(false);

            repository.save(parentProject);
        }

        var now = new Timestamp(System.currentTimeMillis());

        var builder = Project.builder()
            .name(name)
            .type(ProjectType.TASK)
            .parentProject(parentProject)
            .creator(creator)
            .isCompleted(false)
            .createdOn(now);

        if (description != null) {
            builder.description(description);
        }

        repository.save(builder.build());
    }

    @Transactional
    public void changeProjectInfo(long id, ProjectUpdateDto dto) {

        var project = retrieveProjectAndCheck(id);
        modelMapper.map(dto, project);

        repository.save(project);
    }

    @Transactional(readOnly = true)
    public ProjectDetailDto retrieveProjectDetail(long id) {
        var target = retrieveProjectAndCheck(id);
        return this.convertToDetailDto(target);
    }

    @Transactional(readOnly = true)
    public List<ProjectDetailDto> listRootProjects(User user) {

        var userId = user.getId();

        return repository
            .findByParentProjectNullAndCreatorIdOrManagerId(userId, userId)
            .map(this::convertToDetailDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectDetailDto> listSubProjects(long id) {

        var target = retrieveProjectAndCheck(id);

        return target.getSubProjects()
            .stream()
            .map(this::convertToDetailDto)
            .toList();
    }

    @Transactional
    public void newSubProject(long parentId, ProjectCreateDto dto) {

        var user = userService.getUserByContext().orElseThrow();

        var parentProject = this.retrieveProjectAndCheck(parentId, user);
        this.createProject(
            user, parentProject,
            dto.getName(), dto.getDescription()
        );
    }

    @Transactional
    public void markCompleted(long id) {

        var target = retrieveProjectAndCheck(id);

        if (target.getType() != ProjectType.TASK) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Only task can mark completed"
            );
        }

        target.setIsCompleted(true);
        repository.save(target);
    }

    public void deleteProject(long id) {
        var target = retrieveProjectAndCheck(id);
        repository.delete(target);
    }
}
