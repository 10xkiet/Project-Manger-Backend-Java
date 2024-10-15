package com.group8.projectmanager.dtos;

import jakarta.validation.constraints.NotEmpty;

public record ProjectDto(
        @NotEmpty String projectName
        ) {


}
