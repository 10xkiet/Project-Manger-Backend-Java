package com.group8.projectmanager.dtos.project;

import jakarta.validation.constraints.NotEmpty;

import java.sql.Timestamp;

public record ProjectUpdateDto(@NotEmpty String projectName,
                               String projectDesc,
                               Timestamp deadline) {
}
