package com.group8.projectmanager.dtos.project;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
public class ProjectDetailDto {

    private Long id;

    private String name;
    private String description;

    private Timestamp createdOn;
    private Timestamp deadline;

    private List<ProjectSimpleDto> subProjects;
}
