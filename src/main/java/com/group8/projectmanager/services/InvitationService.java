package com.group8.projectmanager.services;

import com.group8.projectmanager.dtos.invitation.InvitationDto;
import com.group8.projectmanager.dtos.invitation.InvitationViewDto;
import com.group8.projectmanager.models.Invitation;
import com.group8.projectmanager.repositories.InvitationRepository;
import com.group8.projectmanager.repositories.ProjectRepository;
import com.group8.projectmanager.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final UserService userService;
    private final UserRepository userRepository;

    private final ProjectService projectService;
    private final ProjectRepository projectRepository;

    private final InvitationRepository invitationRepository;

    private InvitationViewDto convertToDto(Invitation invitation) {
        return new InvitationViewDto(

            invitation.getId(),

            invitation.getSender().getUsername(),
            invitation.getReceiver().getUsername(),

            invitation.isAccepted()
        );
    }

    public void invite(long projectId, InvitationDto invitationDto) {

        var sender = userService.getUserByContext().orElseThrow();
        var targetProject = projectService.retrieveProjectAndCheck(projectId, sender);

        var username = invitationDto.receiver();
        var invitedUser = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var invitation = Invitation.builder()
            .sender(sender)
            .receiver(invitedUser)
            .project(targetProject)
            .build();

        invitationRepository.save(invitation);
    }

    public void accept(long id) {

        Invitation target;

        try {
            target = invitationRepository.getReferenceById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        var user = userService.getUserByContext().orElseThrow();
        var userIsReceiver = target.getReceiver().getId().equals(user.getId());
        if (!userIsReceiver) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        var targetProject = target.getProject();
        targetProject.setManager(user);

        projectRepository.save(targetProject);
    }

    @Transactional(readOnly = true)
    public List<InvitationViewDto> listInvitations() {

        var user = userService.getUserByContext().orElseThrow();

        return invitationRepository.findByReceiverId(user.getId())
            .map(this::convertToDto)
            .toList();
    }
}
