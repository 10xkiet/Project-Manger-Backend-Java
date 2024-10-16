package com.group8.projectmanager.controllers;

import com.group8.projectmanager.dtos.invitation.InvitationDto;
import com.group8.projectmanager.dtos.invitation.InvitationViewDto;
import com.group8.projectmanager.services.InvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping("/projects/{id}/invite/")
    @ResponseStatus(HttpStatus.CREATED)
    public void invite(
        @PathVariable long projectId,
        @Valid @RequestBody InvitationDto dto
    ) {
        invitationService.invite(projectId, dto);
    }

    @GetMapping
    public List<InvitationViewDto> listInvitations() {
        return invitationService.listInvitations();
    }

    @PostMapping("/invitation/{id}/accept/")
    public void acceptInvitation(@PathVariable long id) {
        invitationService.accept(id);
    }
}
