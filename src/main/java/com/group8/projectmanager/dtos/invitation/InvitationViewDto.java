package com.group8.projectmanager.dtos.invitation;

public record InvitationViewDto(

    long id,

    String sender,
    String receiver,

    boolean accepted

) {}
