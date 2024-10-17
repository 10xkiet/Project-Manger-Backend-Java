package com.group8.projectmanager.dtos.project;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.sql.Timestamp;

@Data
public final class ProjectUpdateDto {

    @NotEmpty
    private String name;
    private String description;

    private Timestamp deadline;
}
