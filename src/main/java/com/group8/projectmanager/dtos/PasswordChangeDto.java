package com.group8.projectmanager.dtos;

import jakarta.validation.constraints.NotBlank;

public record PasswordChangeDto(

    @NotBlank
    String oldPassword,

    @NotBlank
    String newPassword

) {}
