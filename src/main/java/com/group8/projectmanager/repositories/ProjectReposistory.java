package com.group8.projectmanager.repositories;
import com.group8.projectmanager.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectReposistory extends JpaRepository<Project,Long> {
    @Override
    Optional<Project> findById(Long aLong);
}
