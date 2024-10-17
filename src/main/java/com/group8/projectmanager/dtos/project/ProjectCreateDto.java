package com.group8.projectmanager.dtos.project;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public final class ProjectCreateDto {

    @NotEmpty
    private String name;

    private String description;
}
