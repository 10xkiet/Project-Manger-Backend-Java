package com.group8.projectmanager.services;

import com.group8.projectmanager.dtos.project.ProjectCreateDto;
import com.group8.projectmanager.dtos.project.ProjectDetailDto;
import com.group8.projectmanager.dtos.project.ProjectSimpleDto;
import com.group8.projectmanager.models.Project;
import com.group8.projectmanager.models.User;
import com.group8.projectmanager.repositories.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ModelMapper modelMapper;
    private final ProjectRepository repository;

    private ProjectSimpleDto convertToDto(Project project) {
        return modelMapper.map(project, ProjectSimpleDto.class);
    }

    private ProjectDetailDto convertToDetailedDto(Project project) {

        var subProjects = project.getSubProjects()
            .stream()
            .map(this::convertToDto)
            .toList();

        return new ProjectDetailDto(

            project.getId(),

            project.getName(),
            project.getDescription(),

            project.getCreatedOn(),
            project.getDeadline(),

            subProjects
        );
    }

    @Transactional(readOnly = true)
    public List<ProjectSimpleDto> listProjects(User user) {
        return repository.findByCreatorIdOrManagerId(user.getId(), user.getId())
            .map(this::convertToDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ProjectDetailDto> retrieveProject(long id) {

        try {

            var target = repository.getReferenceById(id);
            var mapped = convertToDetailedDto(target);

            return Optional.of(mapped);

        } catch (EntityNotFoundException e) {
            return Optional.empty();
        }
    }

    public Project createProject(User creator, ProjectCreateDto dto) {

        var now = new Timestamp(System.currentTimeMillis());

        var newProject = Project.builder()
            .name(dto.getName())
            .creator(creator)
            .createdOn(now)
            .build();

        return repository.save(newProject);
    }
}
