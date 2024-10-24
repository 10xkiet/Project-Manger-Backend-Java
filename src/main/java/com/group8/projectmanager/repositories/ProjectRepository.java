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
        LEFT JOIN p.parentProject parent
        WHERE (parent IS NULL
               OR (parent.creator.id <> ?1 AND parent.manager.id <> ?1))
              AND (p.creator.id = ?1 OR p.manager.id = ?1)
    """)
    Stream<Project> findProjectsWhoseParentHidden(long userId);

    @Query("""
        SELECT COUNT(p) FROM Project p
        INNER JOIN p.subProjects subs
        WHERE p.id = ?1 AND subs.isCompleted = true
    """)
    long countCompletedSubproject(long projectId);
}