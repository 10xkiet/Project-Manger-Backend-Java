package com.group8.projectmanager.services;

import com.group8.projectmanager.dtos.project.ProjectCreateDto;
import com.group8.projectmanager.dtos.project.ProjectDetailDto;
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
import org.springframework.web.ErrorResponseException;

import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ModelMapper modelMapper;
    private final UserService userService;
    private final ProjectRepository repository;

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
        var completed = repository.countCompletedSubproject(project.getId());
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

    private boolean isUserOrManager(Project project, User user) {
        return userService.isEqual(user, project.getCreator())
            || userService.isEqual(user, project.getManager());
    }

    private boolean ableToView(Project project, User user) {

        while (project != null) {

            if (isUserOrManager(project, user)) {
                return true;
            }

            project = project.getParentProject();
        }

        return false;
    }

    private Project findHighestNode(
        Map<Long, Project> disjointSet,
        Project proj, User user
    ) {
        return disjointSet.computeIfAbsent(proj.getId(), (id) -> {

            var parent = proj.getParentProject();

            if (parent == null || !isUserOrManager(parent, user)) {
                return proj;
            }

            return findHighestNode(disjointSet, parent, user);
        });
    }

    private void convertToProject(Project project) {

        project.setType(ProjectType.PROJECT);
        project.setIsCompleted(false);

        repository.save(project);
    }

    public Project retrieveProjectAndCheck(long id, User user) {

        Project target;
        boolean isAbleToView;

        try {

            target = repository.getReferenceById(id);
            isAbleToView = this.ableToView(target, user);

        } catch (EntityNotFoundException e) {
            throw new ErrorResponseException(HttpStatus.NOT_FOUND);
        }

        if (!isAbleToView) {
            throw new ErrorResponseException(HttpStatus.FORBIDDEN);
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

        ProjectType type = ProjectType.TASK;

        if (parentProject == null) {
            type = ProjectType.ROOT;
        } else if (parentProject.getType() == ProjectType.TASK) {
            convertToProject(parentProject);
        }

        var now = new Timestamp(System.currentTimeMillis());

        var builder = Project.builder()
            .name(name)
            .type(type)
            .parentProject(parentProject)
            .creator(creator)
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
    public List<ProjectDetailDto> listAllVisibleProjects(User user) {

        var results = new TreeSet<>(Comparator.comparing(Project::getId));

        Map<Long, Project> disjointSet = new TreeMap<>();

        repository
            .findVisibleProjects(user.getId())
            .map(proj -> findHighestNode(disjointSet, proj, user))
            .forEach(results::add);

        return results.stream()
            .map(proj -> {

                var subprojects = new ArrayList<>(proj.getSubProjects());
                subprojects.add(proj);

                return subprojects;
            })
            .flatMap(List::stream)
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
        createProject(user, parentProject, dto.getName(), dto.getDescription());
    }

    @Transactional
    public void markCompleted(long id) {

        var target = retrieveProjectAndCheck(id);

        if (target.getType() != ProjectType.TASK) {

            var e = new ErrorResponseException(HttpStatus.CONFLICT);
            e.setTitle("Only task can mark completed.");

            throw e;
        }

        target.setIsCompleted(true);
        repository.save(target);
    }

    @Transactional
    public void deleteProject(long id) {

        var user = userService.getUserByContext().orElseThrow();
        var target = retrieveProjectAndCheck(id, user);

        var userIsManager = userService.isEqual(user, target.getManager());

        if (userIsManager) {
            target.setManager(null);
            repository.save(target);
        } else {
            repository.delete(target);
        }
    }
}
