package com.group8.projectmanager.controllers;

import com.group8.projectmanager.dtos.invitation.InvitationDto;
import com.group8.projectmanager.dtos.invitation.InvitationViewDto;
import com.group8.projectmanager.services.InvitationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/")
@SecurityRequirements({
    @SecurityRequirement(name = "basicAuth"),
    @SecurityRequirement(name = "bearerAuth")
})
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping("/projects/{id}/invite/")
    @ResponseStatus(HttpStatus.CREATED)
    public void invite(
        @PathVariable("id") long projectId,
        @Valid @RequestBody InvitationDto dto
    ) {
        invitationService.invite(projectId, dto);
    }

    @GetMapping("/invitations/")
    public List<InvitationViewDto> listInvitations() {
        return invitationService.listInvitations();
    }

    @GetMapping("/myInvitations/")
    public List<InvitationViewDto> listMyInvitations() {
        return invitationService.listMyInvitations();
    }

    @DeleteMapping("/invitations/{id}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInvitation(@PathVariable long id) {
        invitationService.deleteInvitation(id);
    }

    @PostMapping("/invitations/{id}/accept/")
    public void acceptInvitation(@PathVariable long id) {
        invitationService.changeInvitationStatus(id, true);
    }

    @PostMapping("/invitations/{id}/reject/")
    public void rejectInvitation(@PathVariable long id) {
        invitationService.changeInvitationStatus(id, false);
    }
}
