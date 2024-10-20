package com.group8.projectmanager.dtos.token;

import jakarta.validation.constraints.NotEmpty;

public record TokenRefreshRequestDto(

    @NotEmpty
    String refresh

) {}
