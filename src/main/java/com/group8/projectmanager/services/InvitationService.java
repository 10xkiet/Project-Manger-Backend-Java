package com.group8.projectmanager.services;

import com.group8.projectmanager.dtos.invitation.InvitationDto;
import com.group8.projectmanager.dtos.invitation.InvitationViewDto;
import com.group8.projectmanager.models.Invitation;
import com.group8.projectmanager.models.InvitationStatus;
import com.group8.projectmanager.repositories.InvitationRepository;
import com.group8.projectmanager.repositories.ProjectRepository;
import com.group8.projectmanager.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.ErrorResponseException;

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
        if (targetProject.getManager() != null) {
            throw new ErrorResponseException(HttpStatus.CONFLICT);
        }

        var username = invitationDto.receiver();
        var invitedUser = userRepository.findByUsername(username)
            .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));

        var senderIsInvited = userService.isEqual(sender, invitedUser);

        if (senderIsInvited) {
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST);
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

        var target = invitationRepository.getReferenceById(id);

        var user = userService.getUserByContext().orElseThrow();
        var userIsReceiver = userService.isEqual(user, target.getReceiver());

        if (!userIsReceiver) {
            throw new ErrorResponseException(HttpStatus.FORBIDDEN);
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

        var target = invitationRepository.getReferenceById(id);

        var user = userService.getUserByContext().orElseThrow();

        var userIsSender = userService.isEqual(user, target.getSender());
        var userIsReceiver = userService.isEqual(user, target.getReceiver());

        if (!(userIsSender || userIsReceiver)) {
            throw new ErrorResponseException(HttpStatus.FORBIDDEN);
        }

        invitationRepository.delete(target);
    }
}
