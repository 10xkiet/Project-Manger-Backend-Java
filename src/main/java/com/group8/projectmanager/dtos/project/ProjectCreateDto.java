package com.group8.projectmanager.dtos.project;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public final class ProjectCreateDto {

    @NotEmpty
    private String name;

    private String description;

    private Timestamp startedOn;
    private Timestamp deadline;
}
