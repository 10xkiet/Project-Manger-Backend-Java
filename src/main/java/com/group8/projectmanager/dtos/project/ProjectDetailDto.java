package com.group8.projectmanager.dtos.project;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class ProjectDetailDto {

    private Long id;

    private String name;
    private String description;

    private long completedCount;
    private long subProjectCount;

    private Timestamp createdOn;
    private Timestamp deadline;
}
