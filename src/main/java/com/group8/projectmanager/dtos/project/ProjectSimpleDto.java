package com.group8.projectmanager.dtos.project;

import com.group8.projectmanager.models.ProjectType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public final class ProjectSimpleDto {

    private Long id;

    private String name;
    private String description;

    private ProjectType type;
}
