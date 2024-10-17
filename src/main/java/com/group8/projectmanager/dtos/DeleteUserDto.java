package com.group8.projectmanager.dtos;

import jakarta.validation.constraints.NotBlank;

public record DeleteUserDto(

    @NotBlank
    String password

) {}
