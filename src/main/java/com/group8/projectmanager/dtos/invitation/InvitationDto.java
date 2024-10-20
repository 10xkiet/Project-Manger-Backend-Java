package com.group8.projectmanager.dtos.invitation;

import jakarta.validation.constraints.NotEmpty;

public record InvitationDto(

    @NotEmpty
    String receiver,

    @NotEmpty
    String title,

    @NotEmpty
    String description

) {}
