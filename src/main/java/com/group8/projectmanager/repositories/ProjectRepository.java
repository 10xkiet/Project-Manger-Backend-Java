package com.group8.projectmanager.repositories;

import com.group8.projectmanager.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    long countByIdAndSubProjectsIsCompletedTrue(Long id);

    Stream<Project> findByParentProjectNullAndCreatorIdOrManagerId(Long creatorId, Long creatorId1);

    Stream<Project> findByParentProjectId(Long id);
}
