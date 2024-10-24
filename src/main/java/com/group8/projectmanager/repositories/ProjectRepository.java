package com.group8.projectmanager.repositories;

import com.group8.projectmanager.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("""
        SELECT p FROM Project p
        WHERE p.creator.id = ?1 OR p.manager.id = ?1
    """)
    Stream<Project> findProjectsWhereVisible(long userId);

    @Query("""
        SELECT count(p) FROM Project p
        INNER JOIN p.subProjects subProjects
        WHERE p.id = ?1 AND subProjects.isCompleted = true
    """)
    long countCompletedSubproject(long projectId);
}