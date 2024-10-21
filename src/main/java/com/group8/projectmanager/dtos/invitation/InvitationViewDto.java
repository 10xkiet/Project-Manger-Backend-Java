package com.group8.projectmanager.dtos.invitation;

import com.group8.projectmanager.models.InvitationStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class InvitationViewDto {

    private long id;

    private String title;
    private String description;

    private String sender;

    private InvitationStatus status;
}
