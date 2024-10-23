package com.group8.projectmanager.services;

import com.group8.projectmanager.dtos.invitation.InvitationDto;
import com.group8.projectmanager.dtos.invitation.InvitationViewDto;
import com.group8.projectmanager.models.Invitation;
import com.group8.projectmanager.models.InvitationStatus;
import com.group8.projectmanager.repositories.InvitationRepository;
import com.group8.projectmanager.repositories.ProjectRepository;
import com.group8.projectmanager.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final ModelMapper modelMapper;

    private final UserService userService;
    private final UserRepository userRepository;

    private final ProjectService projectService;
    private final ProjectRepository projectRepository;

    private final InvitationRepository invitationRepository;

    private InvitationViewDto convertToDto(Invitation invitation) {

        var result = new InvitationViewDto();

        modelMapper.map(invitation, result);
        result.setSender(invitation.getSender().getUsername());

        return result;
    }

    @Transactional
    public void invite(long projectId, InvitationDto invitationDto) {

        var sender = userService.getUserByContext().orElseThrow();
        var targetProject = projectService.retrieveProjectAndCheck(projectId, sender);

        var username = invitationDto.receiver();
        var invitedUser = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var senderIsInvited = userService.isEqual(sender, invitedUser);

        if (senderIsInvited) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        var invitation = Invitation.builder()

            .title(invitationDto.title())
            .description(invitationDto.description())

            .sender(sender)
            .receiver(invitedUser)

            .status(InvitationStatus.PENDING)
            .project(targetProject)

            .build();

        invitationRepository.save(invitation);
    }

    @Transactional
    public void changeInvitationStatus(long id, boolean isAccept) {

        Invitation target;
        boolean userIsReceiver;

        var user = userService.getUserByContext().orElseThrow();

        try {
            target = invitationRepository.getReferenceById(id);
            userIsReceiver = userService.isEqual(user, target.getReceiver());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (!userIsReceiver) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (isAccept) {

            var targetProject = target.getProject();
            targetProject.setManager(user);

            projectRepository.save(targetProject);

            target.setStatus(InvitationStatus.ACCEPTED);
            invitationRepository.save(target);

        } else {

            target.setStatus(InvitationStatus.REJECTED);
            invitationRepository.save(target);
        }
    }

    @Transactional(readOnly = true)
    public List<InvitationViewDto> listInvitations() {

        var user = userService.getUserByContext().orElseThrow();

        return invitationRepository.findByReceiverId(user.getId())
            .map(this::convertToDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<InvitationViewDto> listMyInvitations() {

        var user = userService.getUserByContext().orElseThrow();

        return invitationRepository.findBySenderId(user.getId())
            .map(this::convertToDto)
            .toList();
    }

    @Transactional
    public void deleteInvitation(long id) {

        Invitation target;
        boolean userIsSender;

        var user = userService.getUserByContext().orElseThrow();

        try {
            target = invitationRepository.getReferenceById(id);
            userIsSender = userService.isEqual(user, target.getSender());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (!userIsSender) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        invitationRepository.delete(target);
    }
}
