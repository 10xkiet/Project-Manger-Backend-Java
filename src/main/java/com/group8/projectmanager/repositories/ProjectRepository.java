package com.group8.projectmanager.repositories;

import com.group8.projectmanager.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Stream<Project> findByParentProjectNullAndCreatorIdOrManagerId(long creatorId, long managerId);
    Stream<Project> findByCreatorIdOrManagerId(long creatorId, long managerId);
    long countByIdAndSubProjectsIsCompletedTrue(long id);
}
